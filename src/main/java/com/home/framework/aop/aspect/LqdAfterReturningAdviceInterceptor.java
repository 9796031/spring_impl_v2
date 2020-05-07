package com.home.framework.aop.aspect;

import com.home.framework.aop.intercept.LqdMethodInterceptor;
import com.home.framework.aop.support.LqdReflectiveMethodInvocation;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;

/**
 * @author liqingdong
 * AOP after return操作
 */
@Log4j2
@SuppressWarnings("unused")
public class LqdAfterReturningAdviceInterceptor extends LqdAbstractAdvice implements LqdMethodInterceptor {
    public LqdAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private Object afterReturning(LqdJoinPoint joinPoint, Object returnValue, Throwable t) throws Throwable {
        log.info("after returning invoke successful !!!");
        return invokeMethod(joinPoint, returnValue, t);
    }

    @Override
    public Object invoke(LqdReflectiveMethodInvocation methodInvocation) throws Throwable{
        this.joinPoint = methodInvocation;
        Object returnValue = methodInvocation.proceed();
        return afterReturning(methodInvocation, returnValue, null);
    }
}
