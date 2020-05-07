package com.home.framework.beans.config;

/**
 * @author liqingdong
 * 负责存储配置信息的顶层接口
 */
public interface LqdBeanDefinition {

    /**
     * 设置bean定义的类名
     * @param beanClassName 要加载的类名(com.home.service.UserService)
     */
    void setBeanClassName(String beanClassName);

    /**
     * 获取bean类名
     * @return beanClassName
     */
    String getBeanClassName();

    /**
     * 用于存储和获取beanDefinition
     * @param beanFactoryName key名(userService)
     */
    void setBeanFactoryName(String beanFactoryName);

    /**
     * 返回首字母小写后的类名
     * @return 首字母小写后的类名
     */
    String getBeanFactoryName();
    /**
     * 是否延迟加载
     * @return 返回是否延迟加载
     */
    boolean isLazyInit();

    /**
     * 设置是否需要延迟加载
     * @param lazyInit 是否延迟加载
     */
    void setLazyInit(boolean lazyInit);
}
