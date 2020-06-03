package com.home.framework.context.support;

import com.home.framework.aop.LqdAopProxy;
import com.home.framework.aop.LqdJdkDynamicAopProxy;
import com.home.framework.aop.config.LqdAopConfig;
import com.home.framework.aop.support.LqdAdvisedSupport;
import com.home.framework.beans.LqdBeanWrapper;
import com.home.framework.beans.config.LqdBeanDefinition;
import com.home.framework.beans.config.LqdBeanDefinitionReader;
import com.home.framework.beans.config.support.LqdPropertiesBeanDefinitionReader;
import com.home.framework.beans.support.LqdBeanWrapperImpl;
import com.home.framework.beans.support.LqdDefaultListableBeanFactory;
import com.home.framework.context.LqdAbstractApplicationContext;
import com.home.framework.stereotype.LQDAutowired;
import com.home.framework.stereotype.LQDController;
import com.home.framework.stereotype.LQDService;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liqingdong
 * @desc IOC容器实现（应定义成接口，可扩展xml，properties等）
 */
public class LqdApplicationContext extends LqdAbstractApplicationContext {

    /**
     * 配置信息
     */
    private final String configLocation;

    private static final String POINT_CUT = "pointCut";

    private static final String ASPECT_BEFORE = "aspectBefore";

    private static final String ASPECT_AFTER = "aspectAfter";

    private static final String ASPECT_AFTER_THROW = "aspectAfterThrow";

    private static final String ASPECT_AFTER_THROWING_NAME = "aspectAfterThrowingName";

    private static final String ASPECT_CLASS = "aspectClass";

    /** 单例对象实例 beanFactoryName or beanClassName -> instance */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    /** 存储bean配置信息 beanFactoryName or beanClassName -> beanDefinition */
    private Map<String, LqdBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private LqdBeanDefinitionReader reader;

    public LqdApplicationContext(String configLocation) {
        this.configLocation = configLocation;
        refresh();
    }

    public String getProperties(String key) {
        return reader.getProperties(key);
    }

    /**
     * 初始化加载容器
     * 主要完成:定位,加载,注册
     */
    @Override
    protected void refresh() {
        // 1. 加载配置文件
        reader = LqdPropertiesBeanDefinitionReader.getInstance();
        List<LqdBeanDefinition> lqdBeanDefinitions = reader.loadBeanDefinitions(configLocation);
        // 2. 注册beanDefinition
        this.beanDefinitionMap = LqdDefaultListableBeanFactory.getInstance().registerBeanDefinitions(lqdBeanDefinitions);
        // 3. 扫秒包,实例对象
        doCreateBean(beanDefinitionMap);
    }

    private void doCreateBean(Map<String, LqdBeanDefinition> beanDefinitionMap) {
        for (Map.Entry<String, LqdBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            LqdBeanDefinition definition = entry.getValue();
            if (!definition.isLazyInit()) {
                String beanClassName = definition.getBeanClassName();
                try {
                    Class<?> clazz = Class.forName(beanClassName);
                    if (clazz.isInterface()) {continue;}
                    if (clazz.isAnnotation()) {continue;}
                    getBean(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public <T> T getBean(Class<T> clazz) {
        String beanClassName = clazz.getName();
        LqdBeanDefinition beanDefinition = beanDefinitionMap.get(beanClassName);
        // 初始化bean
        LqdBeanWrapper bw = initiateBean(beanClassName, beanDefinition);
        if (bw == null) {return null;}
        // 注入bean
        populateBean(beanClassName, beanDefinition, bw);
        return (T) bw.getWrappedInstance();
    }

    /**
     * 循环依赖注入问题
     * class A {B b;}
     * class B {A a;}
     * 即:先有鸡先有蛋问题如何解决
     * spring解决方案,将初始化和注入拆开,拆开后的注入就不会存在这种问题
     * @param beanClassName
     * @return
     */
    @Override
    public Object getBean(String beanClassName) {
        LqdBeanDefinition beanDefinition = beanDefinitionMap.get(beanClassName);
        if (beanDefinition == null) {return null;}
        // 初始化bean
        LqdBeanWrapper bw = initiateBean(beanClassName, beanDefinition);
        if (bw == null) {return null;}
        // 注入bean
        populateBean(beanClassName, beanDefinition, bw);
        return bw;
    }

    /**
     * 实例化bean, 并且包装bean
     * @param beanClassName 类名
     * @param beanDefinition bean配置
     * @return
     */

    private LqdBeanWrapper initiateBean(String beanClassName, LqdBeanDefinition beanDefinition) {
        try {
            Object o;
            if (singletonObjects.containsKey(beanClassName)) {
                o = singletonObjects.get(beanClassName);
            } else {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                o = clazz.newInstance();
                // AOP //设置代理
                LqdAdvisedSupport advised = instantiationAopConfig(beanDefinition);
                advised.setTargetClass(clazz);
                advised.setTargetResource(o);
                // 如果符合pointCut规则, 则创建代理对象
                if (advised.pointCutMatch()) {
                    o = createProxy(advised).getProxy();
                }
                singletonObjects.put(beanClassName, o);
                singletonObjects.put(beanDefinition.getBeanFactoryName(), o);
            }
            LqdBeanWrapperImpl beanWrapper = new LqdBeanWrapperImpl();
            beanWrapper.setWrappedInstance(o);
            return beanWrapper;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 创建代理对象
     * @param advised
     * @return
     */
    private LqdAopProxy createProxy(LqdAdvisedSupport advised) {
        // 校验是否满足JDK代理条件
        if (advised.getTargetClass().getInterfaces().length > 0) {
            return new LqdJdkDynamicAopProxy(advised);
        }
        // 应该是CgLib方式处理
        return new LqdJdkDynamicAopProxy(advised);
    }

    /**
     * 读取AOP配置
     * @param beanDefinition
     * @return
     */
    private LqdAdvisedSupport instantiationAopConfig(LqdBeanDefinition beanDefinition) {
        LqdAopConfig config = new LqdAopConfig();
        config.setPointCut(getProperties(POINT_CUT));
        config.setAspectBefore(getProperties(ASPECT_BEFORE));
        config.setAspectAfter(getProperties(ASPECT_AFTER));
        config.setAspectAfterThrow(getProperties(ASPECT_AFTER_THROW));
        config.setAspectAfterThrowingName(getProperties(ASPECT_AFTER_THROWING_NAME));
        config.setAspectClass(getProperties(ASPECT_CLASS));
        return new LqdAdvisedSupport(config);
    }

    /**
     * 装配bean
     * @param beanName
     * @param mbd
     * @param bw
     */
    protected void populateBean(String beanName, LqdBeanDefinition mbd, LqdBeanWrapper bw) {
        Class<?> wrappedClass = bw.getWrappedClass();
        Object wrappedInstance = bw.getWrappedInstance();
        if (wrappedClass.isAnnotationPresent(LQDController.class)
            || wrappedClass.isAnnotationPresent(LQDService.class)) {
            Field[] fields = wrappedClass.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(LQDAutowired.class)) {continue;}
                LQDAutowired autowired = field.getAnnotation(LQDAutowired.class);
                String value = autowired.value();
                field.setAccessible(true);
                if (StringUtils.isNotEmpty(value)) {
                    Object o = this.singletonObjects.get(value);
                    if (o == null) {
                        doFieldSetValue(field, wrappedInstance);
                    } else {
                        try {
                            field.set(wrappedInstance, o);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    doFieldSetValue(field, wrappedInstance);
                }
            }
        }
    }

    public Object getInstance(String beanClassName) {
        return this.singletonObjects.get(beanClassName);
    }

    private void doFieldSetValue(Field field, Object obj) {
        Object o = this.singletonObjects.get(field.getName());
        if (o != null) {
            try {
                field.set(obj, o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String name = field.getType().getName();
            System.out.println(name);
        }
    }

    /**
     * 拷贝ListableBeanFactory接口方法
     * @return
     */
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }
}
