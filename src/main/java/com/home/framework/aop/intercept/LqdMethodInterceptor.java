package com.home.framework.aop.intercept;

import com.home.framework.aop.support.LqdReflectiveMethodInvocation;

/**
 * @author liqingdong
 * 拦截器顶层接口, 每个AOP配置的点都要实现该接口
 */
public interface LqdMethodInterceptor {

    /**
     * 拦截器调用方法
     * @param methodInvocation
     * @return
     */
    Object invoke(LqdReflectiveMethodInvocation methodInvocation) throws Throwable;
}
