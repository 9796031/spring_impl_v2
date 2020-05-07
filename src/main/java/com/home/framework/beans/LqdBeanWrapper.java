package com.home.framework.beans;

/**
 * @author liqingdong
 * @desc bean实例包装器顶层接口
 */
public interface LqdBeanWrapper {

    /**
     * 如果是单例对象,可以直接返回
     * @return
     */
    Object getWrappedInstance();

    /**
     * 如果不是单例对象可以返回class对象
     * @return
     */
    Class<?> getWrappedClass();

}
