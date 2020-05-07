package com.home.framework.beans.support;

import com.home.framework.beans.config.LqdBeanDefinition;

import java.util.List;
import java.util.Map;

/**
 * @author liqingdong
 * beanDefinition注册器顶层接口
 * 用来将BeanDefinitionReader读取到的信息注册到BeanDefinitionMap中
 */
public interface LqdBeanFactoryRegister {

    /**
     * 将加载配置文件的属性包装被注册
     * @param  beanDefinitions 属性对象
     * @return
     */
    Map<String, LqdBeanDefinition> registerBeanDefinitions(List<LqdBeanDefinition> beanDefinitions);
}
