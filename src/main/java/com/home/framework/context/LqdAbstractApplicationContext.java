package com.home.framework.context;

import com.home.framework.beans.LqdBeanFactory;

/**
 * @author liqingdong
 * @desc 主要完成容器初始化操作 <br/>
 * IOC容器实现的顶层设计（可以扩展xml，properties等容器）
 * 同时实现DefaultListableBeanFactory beanFactory单例工厂默认实现
 *
 * 模板设计模式, 提供抽象方法由子类实现
 */
public abstract class LqdAbstractApplicationContext implements LqdBeanFactory {

    /**
     * 初始化容器
     */
    protected void refresh(){

    }

}
