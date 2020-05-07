package com.home.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author liqingdong
 * 提取公共方法, 切面抽象实现
 */
public abstract class LqdAbstractAdvice {

    /**
     * 配置的切面处理类方法(通知)
     */
    private final Method aspectMethod;
    /**
     * 切面(切面处理类)
     */
    private final Object aspectTarget;

    protected LqdJoinPoint joinPoint;

    public LqdAbstractAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    /**
     * 方法调用实现
     *
     * @param joinPoint   连接点
     * @param returnValue 返回值
     * @param t           抛出的异常
     * @return 执行后返回值
     * @throws Throwable 调用异常
     */
    public Object invokeMethod(LqdJoinPoint joinPoint, Object returnValue, Throwable t) throws Throwable {
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        if (parameterTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == LqdJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                } else if (parameterTypes[i] == Throwable.class) {
                    args[i] = t;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }
}
