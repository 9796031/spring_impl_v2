package com.home.framework.beans.config.support;

import com.home.framework.beans.config.LqdBeanDefinition;

/**
 * @author liqingdong
 * bean配置信息实现类
 */
public class LqdRootBeanDefinition implements LqdBeanDefinition {

    private String beanClassName;

    private String beanFactoryName;

    private boolean lazyInit = true;

    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    @Override
    public String getBeanClassName() {
        return beanClassName;
    }

    @Override
    public void setBeanFactoryName(String beanFactoryName) {
        this.beanFactoryName = beanFactoryName;
    }

    @Override
    public String getBeanFactoryName() {
        return this.beanFactoryName;
    }

    @Override
    public boolean isLazyInit() {
        return this.lazyInit;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

}
