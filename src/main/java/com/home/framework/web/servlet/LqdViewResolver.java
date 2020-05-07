package com.home.framework.web.servlet;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liqingdong
 * @desc 视图解析器, 主要负责存储视图(文件)
 */
@Data
public class LqdViewResolver {

    private String filePath;

    private String fileName;

    private final List<LqdViewResolver> viewResolvers = new ArrayList<>();

    public LqdViewResolver(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public LqdView resolveViewName(String viewName) {

        File file = new File(this.filePath);
        return new LqdView(file);
    }
}
