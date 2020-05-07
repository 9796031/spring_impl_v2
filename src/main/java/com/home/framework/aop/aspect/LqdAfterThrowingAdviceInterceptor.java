package com.home.framework.aop.aspect;

import com.home.framework.aop.intercept.LqdMethodInterceptor;
import com.home.framework.aop.support.LqdReflectiveMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author liqingdong
 * AOP 异常拦截
 */
public class LqdAfterThrowingAdviceInterceptor extends LqdAbstractAdvice implements LqdMethodInterceptor {

    private String throwingName;

    public LqdAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(LqdReflectiveMethodInvocation methodInvocation) throws Throwable{
        try {
            return methodInvocation.proceed();
        } catch (Throwable t) {
            invokeMethod(methodInvocation, null, t.getCause());
            throw t;
        }
    }

    public LqdAfterThrowingAdviceInterceptor setThrowingName(String throwingName) {
        this.throwingName = throwingName;
        return this;
    }
}
