package com.home.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author liqingdong
 * AOP连接点
 */
public interface LqdJoinPoint {

    /**
     * 执行器链执行方法, 调用链模式
     * @return 方法运行结果
     * @throws Throwable e
     */
    Object proceed() throws Throwable;

    /**
     * 返回当前类对象
     * @return 当前对象
     */
    Object getThis();

    /**
     * 返回具体代理方法
     * @return 返回实际代理方法
     */
    Method getMethod();

    /**
     * 返回实参
     * @return 实参
     */
    Object[] getArguments();
}
