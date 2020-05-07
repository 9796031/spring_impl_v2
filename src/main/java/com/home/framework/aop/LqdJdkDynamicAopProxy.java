package com.home.framework.aop;

import com.home.framework.aop.support.LqdAdvisedSupport;
import com.home.framework.aop.support.LqdReflectiveMethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author liqingdong
 * JDK代理
 */
public class LqdJdkDynamicAopProxy implements LqdAopProxy, InvocationHandler {


    private LqdAdvisedSupport advised;

    public LqdJdkDynamicAopProxy(LqdAdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.advised.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 拦截器链
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());
        // 调用链设计模式
        LqdReflectiveMethodInvocation methodInvocation = new LqdReflectiveMethodInvocation(proxy, null,
                method, args, this.advised.getTargetClass(), chain);
        // 执行拦截器链
        return methodInvocation.proceed();
    }
}
