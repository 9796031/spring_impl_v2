package com.home.framework.aop.support;

import com.home.framework.aop.aspect.LqdJoinPoint;
import com.home.framework.aop.intercept.LqdMethodInterceptor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author liqingdong
 * ReflectiveMethodInvocation
 * 调用链模式, 主要完成代理增强后顺序执行增强&方法
 */
@Data
public class LqdReflectiveMethodInvocation implements LqdJoinPoint {

    private final Object proxy;
    private final Object target;
    private final Class<?> targetClass;
    private final Method method;
    private final Object[] arguments;
    private final List<Object> interceptorsAndDynamicMethodMatchers;

    private int currentInterceptorIndex = -1;

    /**
     *
     * @param proxy 代理对象
     * @param target 目标对象
     * @param method 实际代理方法
     * @param arguments 参数列表
     * @param targetClass 目标代理类
     * @param interceptorsAndDynamicMethodMatchers 执行器链
     */
    public LqdReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
                                         Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    @Override
    public Object proceed() throws Throwable {
        // 如果一个调用链都没有或者执行到最后一个调用链
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return method.invoke(target, arguments);
        }

        Object interceptorOrInterceptionAdvice =
                this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        //如果要动态匹配joinPoint
        if (interceptorOrInterceptionAdvice instanceof LqdMethodInterceptor) {
            LqdMethodInterceptor dm =
                    (LqdMethodInterceptor) interceptorOrInterceptionAdvice;
            //动态匹配：运行时参数是否满足匹配条件
            return dm.invoke(this);
        } else {
            // 执行下一个拦截器
            return proceed();
        }
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public Method getMethod() {
        return method;
    }
}
