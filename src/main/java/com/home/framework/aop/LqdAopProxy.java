package com.home.framework.aop;

/**
 * @author liqingdong
 * AOP顶层类(可扩展为JDK代理,CGLib代理)
 */
public interface LqdAopProxy {

    /**
     * 获取代理对象
     * @return 对象实例
     */
    <T> T getProxy();

    /**
     * 根据classLoader获取代理对象实例
     * @param classLoader classLoader
     * @return 对象实例
     */
    <T> T getProxy(ClassLoader classLoader);
}
