package com.home.framework.beans.support;

import com.home.framework.beans.LqdBeanFactory;
import com.home.framework.beans.config.LqdBeanDefinition;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liqingdong
 * spring容器基础实现, 主要完成通过bean配置注册到map中
 */
public class LqdDefaultListableBeanFactory implements LqdBeanFactory, LqdBeanFactoryRegister {

    /**
     * 存储beanDefinition(bean配置信息)
     */
    private final Map<String, LqdBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private LqdDefaultListableBeanFactory() {}
    /**
     * 获取bean实例
     * @param beanName beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {
        return null;
    }

    @Override
    public Map<String, LqdBeanDefinition> registerBeanDefinitions(List<LqdBeanDefinition> beanDefinitions) {
        for (LqdBeanDefinition beanDefinition : beanDefinitions) {
//            beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanFactoryName(), beanDefinition);
        }
        return beanDefinitionMap;
    }

    public static LqdDefaultListableBeanFactory getInstance() {
        return LQDDefaultListableBeanFactoryEnum.INSTANCE.beanFactory;
    }

    private enum LQDDefaultListableBeanFactoryEnum {
        INSTANCE;
        private final LqdDefaultListableBeanFactory beanFactory = new LqdDefaultListableBeanFactory();
    }
}
