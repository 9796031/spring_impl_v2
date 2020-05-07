package com.home.framework.beans;

/**
 * @author liqingdong
 * @desc 单例工厂的顶层接口, applicationContext顶层接口
 */
public interface LqdBeanFactory {

    /**
     * 获取bean实例, 当bean为延迟加载时, 完成bean初始化操作
     * @param name beanName bean名称
     * @return Object 对象实例
     */
    Object getBean(String name);
}
