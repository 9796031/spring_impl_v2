package com.home.framework.aop.aspect;

import com.home.framework.aop.intercept.LqdMethodInterceptor;
import com.home.framework.aop.support.LqdReflectiveMethodInvocation;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;

/**
 * @author liqingdong
 * @desc AOP before处理
 *
 */
@Log4j2
@SuppressWarnings("unused")
public class LqdMethodBeforeAdviceInterceptor extends LqdAbstractAdvice implements LqdMethodInterceptor {

    public LqdMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        log.info("AOP before invoke successful !!!");
        invokeMethod(joinPoint, null, null);
    }

    @Override
    public Object invoke(LqdReflectiveMethodInvocation methodInvocation) throws Throwable {
        super.joinPoint = methodInvocation;
        // 从被植入的代码中拿到jointPoint LQDReflectiveMethodInvocation本身就是一个joinPoint
        before(methodInvocation.getMethod(), methodInvocation.getArguments(), methodInvocation.getTarget());
        return methodInvocation.proceed();
    }
}
