package com.home.framework.aop.support;

import com.home.framework.aop.config.LqdAopConfig;
import com.home.framework.aop.aspect.LqdAfterReturningAdviceInterceptor;
import com.home.framework.aop.aspect.LqdAfterThrowingAdviceInterceptor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author liqingdong
 * @desc 切面处理类
 */
@Data
public class LqdAdvisedSupport {

    private Class<?> targetClass;

    private Object targetResource;

    private Pattern pointCutPattern;
    /** AOP配置信息 */
    private LqdAopConfig config;
    /**
     * copy Map<MethodCacheKey, List<Object>> methodCache
     * 方法->执行器链
     */
    private Map<Method, List<Object>> methodCache = new HashMap<>();
    public LqdAdvisedSupport(LqdAopConfig config) {
        this.config = config;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        config.setPointCut("class " + config.getPointCut());
        try {
            // 获取配置的切面处理类
            Class<?> clazz = Class.forName(config.getAspectClass());
            Method[] aspectMethods = clazz.getDeclaredMethods();
            Object aspect = clazz.newInstance();
            Map<String, Method> aspectMethodMap = new HashMap<>(aspectMethods.length);
            for (Method method : aspectMethods) {
                String methodName = method.getName();
                if (methodName.contains("throws")) {
                    methodName = methodName.substring(0, methodName.lastIndexOf("throws"));
                }
                aspectMethodMap.put(methodName, method);
            }
            // 把方法封装成执行器链
            for (Method method : this.targetClass.getDeclaredMethods()) {
                // 执行器链
                List<Object> chain = new ArrayList<>(4);
                String aspectBefore = config.getAspectBefore();
                if (StringUtils.isNotEmpty(aspectBefore)) {
                    Method aspectMethod = aspectMethodMap.get(aspectBefore);
                    chain.add(new LqdAfterReturningAdviceInterceptor(aspectMethod, aspect));
                }
                String aspectAfter = config.getAspectAfter();
                if (StringUtils.isNotEmpty(aspectAfter)) {
                    Method aspectMethod = aspectMethodMap.get(aspectAfter);
                    chain.add(new LqdAfterReturningAdviceInterceptor(aspectMethod, aspect));
                }
                String afterThrowing = config.getAspectAfterThrow();
                if (StringUtils.isNotEmpty(afterThrowing)) {
                    Method aspectMethod = aspectMethodMap.get(afterThrowing);
                    chain.add(new LqdAfterThrowingAdviceInterceptor(aspectMethod, aspect).setThrowingName(config.getAspectAfterThrowingName()));
                }
                methodCache.put(method, chain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 匹配切入点对应的类
     * @return 是否需要代理
     */
    public boolean pointCutMatch() {
        pointCutPattern = Pattern.compile(config.getPointCut());
        return pointCutPattern.matcher(this.targetClass.toString()).matches();
    }
    /**
     * 获取到方法上的所有Interceptor列表
     * @param method 目标方法
     * @param targetClass 目标类
     * @return 执行器链
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        return methodCache.get(method);
    }
}
