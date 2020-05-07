package com.home.framework.web.servlet;

import lombok.Data;

import java.util.Map;

/**
 * @author liqingdong
 * @desc 数据and视图类
 */
@Data
public class LqdModelAndView {
    /** 视图名称 */
    private String viewName;
    /** 响应数据 */
    private Map<String, Object> model;

    public LqdModelAndView() {}

    public LqdModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public LqdModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }
}