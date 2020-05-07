package com.home.framework.beans.config.support;

import com.home.framework.beans.config.LqdBeanDefinition;
import com.home.framework.beans.config.LqdBeanDefinitionReader;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author liqingdong
 * properties属性加载实现
 * 主要完成加载和注册功能（注册：转换成beanDefinition对象）
 */
public class LqdPropertiesBeanDefinitionReader implements LqdBeanDefinitionReader {

    /** 需要扫秒的包 */
    private final String SCAN_PACKAGE = "scanPackage";

    /** applicationContext配置信息 */
    private Properties applicationContextProp;

    /** 需要加载的类名 */
    private final List<String> beanClassNames = new ArrayList<>();

    private LqdPropertiesBeanDefinitionReader() {}

    public static LqdPropertiesBeanDefinitionReader getInstance() {
        return LQDPropertiesBeanDefinitionReaderEnum.INSTANCE.reader;
    }

    @Override
    public List<LqdBeanDefinition> loadBeanDefinitions(String... locations) {
        String applicationContextFilePath = locations[0].replace("classpath:", "");
        // 加载applicationContext配置文件
        loadProperties(applicationContextFilePath);

        // 扫秒需要加载的类
        String scanPackage = (String)applicationContextProp.get(SCAN_PACKAGE);
        doScanner(scanPackage);

        // 封装beanDefinition
        return doLoadBeanDefinition();
    }

    @Override
    public String getProperties(String key) {
        return applicationContextProp.getProperty(key);
    }

    /**
     * 封装bean配置信息
     * @return bean配置信息
     */
    private List<LqdBeanDefinition> doLoadBeanDefinition() {
        List<LqdBeanDefinition> beanDefinitions = new ArrayList<>();
        try {
            for (String beanClassName : beanClassNames) {
                Class<?> clazz = Class.forName(beanClassName);
                if (clazz.isInterface()) {continue;}
                if (clazz.isAnnotation()) {continue;}
                LqdBeanDefinition beanDefinition = new LqdRootBeanDefinition();
                beanDefinition.setBeanClassName(beanClassName);
                beanDefinition.setBeanFactoryName(toLowerFirstCase(clazz.getSimpleName()));
                beanDefinitions.add(beanDefinition);
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> cls : interfaces) {
                    LqdBeanDefinition bd = new LqdRootBeanDefinition();
                    bd.setBeanClassName(beanClassName);
                    bd.setBeanFactoryName(toLowerFirstCase(cls.getSimpleName()));
                    beanDefinitions.add(bd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanDefinitions;
    }

    /**
     * 首字母转小写
     * @param className 类名
     * @return 首字母小写
     */
    private String toLowerFirstCase(String className) {
        if (StringUtils.isBlank(className)) {
            return className;
        }
        char[] chars = className.toCharArray();
        chars[0] = chars[0] += 32;
        return new String(chars);
    }

    /**
     * 加载applicationContext配置
     * @param applicationContextFilePath 配置文件路径
     */
    private void loadProperties(String applicationContextFilePath) {
        if (applicationContextProp != null) { return; }
        // 处理加载
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(applicationContextFilePath);
            applicationContextProp = new Properties();
            applicationContextProp.load(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫秒需要加载的类
     * @param basePath 需要扫秒的包
     */
    private void doScanner(String basePath) {
        URL resource = this.getClass().getResource(("/" + basePath).replaceAll("\\.", "/"));
        File classPath = new File(resource.getFile());
        Objects.requireNonNull(classPath);
        File[] files = classPath.listFiles();
        Objects.requireNonNull(files);
        for (File file : files) {
            if (file.isDirectory()) {
                doScanner(basePath + "/" + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) { continue;}
                String beanClassName = (basePath + "/" + file.getName().replace(".class", ""));
                beanClassNames.add(beanClassName.replaceAll("/", "\\."));
            }
        }
    }

    private enum LQDPropertiesBeanDefinitionReaderEnum {
        /** 获取LQDPropertiesBeanDefinitionReader单例对象*/
        INSTANCE;
        /** prop属性对象*/
        private final LqdPropertiesBeanDefinitionReader reader = new LqdPropertiesBeanDefinitionReader();
    }

}
