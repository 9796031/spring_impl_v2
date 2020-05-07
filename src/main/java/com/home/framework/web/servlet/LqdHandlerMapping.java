package com.home.framework.web.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author liqingdong
 * @desc 处理器映射器, 映射实际执行的controller具体调用方法
 */
@Data
public class LqdHandlerMapping {

    /** 调用实例 */
    private Object controller;
    /** 真正调用的方法 */
    private Method method;
    /** URL正则(URI) */
    private Pattern pattern;

    public LqdHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
