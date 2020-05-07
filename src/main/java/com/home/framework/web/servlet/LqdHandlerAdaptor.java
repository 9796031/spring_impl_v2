package com.home.framework.web.servlet;

import com.home.framework.beans.LqdBeanWrapper;
import com.home.framework.stereotype.LQDRequestParam;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liqingdong
 * @desc 将请求参数和handlerMapping实参对应, 并调用handlerMapping处理
 * 适配器模式
 */
public class LqdHandlerAdaptor {

    /** 参数名->位置 */
    private Map<String, Integer> paramIndexMapping = new HashMap<>();

    /**
     * 封装参数并调用handlerMapping处理
     * @param request
     * @param response
     * @param handler
     * @return
     */
    public LqdModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        LqdHandlerMapping hm = (LqdHandlerMapping) handler;
        // 获取方法入参位置
        doParamIndexMapping(hm);
        // 实参和入参对应, 并获取入参值
        Object[] paramValues = doSetParamValue(request, response, hm);
        LqdBeanWrapper beanWrapper = (LqdBeanWrapper) hm.getController();
        Object returnValue = hm.getMethod().invoke(beanWrapper.getWrappedInstance(), paramValues);
        if (returnValue == null || returnValue instanceof Void) {return null;}
        if (returnValue instanceof LqdModelAndView) {
            return (LqdModelAndView) returnValue;
        }
        return null;
    }

    /**
     * 将请求参数列表的值设置到方法入参
     * @param req
     * @param resp
     * @param hm
     */
    private Object[] doSetParamValue(HttpServletRequest req, HttpServletResponse resp, LqdHandlerMapping hm) {
        Class<?>[] parameterTypes = hm.getMethod().getParameterTypes();
        // 实参列表
        Object[] paramValues = new Object[parameterTypes.length];
        // 获取到request参数
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");
            if (!paramIndexMapping.containsKey(param.getKey())) {continue;}
            int idx = paramIndexMapping.get(param.getKey());
            paramValues[idx] = convert(value, parameterTypes[idx]);
        }

        String requestClassName = HttpServletRequest.class.getName();
        if (paramIndexMapping.containsKey(requestClassName)) {
            int reqIdx = paramIndexMapping.get(requestClassName);
            paramValues[reqIdx] = req;
        }
        String responseClassName = HttpServletResponse.class.getName();
        if (paramIndexMapping.containsKey(responseClassName)) {
            int respIdx = paramIndexMapping.get(responseClassName);
            paramValues[respIdx] = resp;
        }
        return paramValues;
    }

    private Object convert(String value, Class<?> type) {
        if (type == Integer.class) {
            return Integer.valueOf(value);
        } else if (type == Long.class) {
            return Long.getLong(value);
        } else if (type == String.class) {
            return value;
        } else {
            return null;
        }
    }

    /**
     * 获取到handlerMapping中方法添加了requestParam注解的参数
     * 并且将参数名和位置保存到paramIndexMapping
     * @param hm
     */
    private void doParamIndexMapping(LqdHandlerMapping hm) {
        Method method = hm.getMethod();
        // 因为一个参数可能有多个注解, 一个方法有有多个参数, 所以获取到的是一个二位数组
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation requestParam : annotations[i]) {
                if (!(requestParam instanceof LQDRequestParam)) {continue;}
                String value = ((LQDRequestParam) requestParam).value();
                if (StringUtils.isNotEmpty(value)) {
                    paramIndexMapping.put(value, i);
                }
            }
        }

        // 提取方法中的request和response参数
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class
            || parameterType == HttpServletResponse.class) {
                paramIndexMapping.put(parameterType.getName(), i);
            }
        }
    }
}
