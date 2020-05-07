package com.home.framework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liqingdong
 * @desc 视图, 主要负责将响应数据进行渲染到web页面
 */
public class LqdView {

    private static final String CONTENT_TYPE = "text/html;charset=utf-8";

    private File view;

    public LqdView(File view) {
        this.view = view;
    }

    /**
     * 渲染结果集
     * @param model
     * @param request
     * @param response
     */
    public void render(Map<String, ?> model,
                @SuppressWarnings("unused") HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.view));
            String line;
            while ((line = br.readLine()) != null) {
                line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                // 匹配&{} []+表示任意字符 ^\\} 表示以}结尾, CASE_INSENSITIVE 不区分大小写
                Pattern pattern = Pattern.compile("&\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String paramNameAndSymbol = matcher.group();
                    String paramName = paramNameAndSymbol.replaceAll("&\\{|\\}", "");
                    Object value = model.get(paramName);
                    if (value == null) {continue;}
                    // 处理异常返回的特殊字符
                    line = matcher.replaceAll(replaceSpecialSym(value.toString()));
                }
                sb.append(line);
            }
            response.getWriter().write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理异常返回的特殊字符
     * @param str
     * @return
     */
    private String replaceSpecialSym(String str) {
        //(    [     {    /    ^    -    $     ¦    }    ]    )    ?    *    +    .
        return str.replace("(", "\\(")
                .replace("[", "\\[")
                .replace("^", "\\^")
                .replace("-", "\\-")
                .replace("$", "\\$")
                .replace("|", "\\|")
                .replace("}", "\\}")
                .replace("]", "\\]")
                .replace(")", "\\)")
                .replace("?", "\\?")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace(".", "\\.")
                .replace(",", "\r\n");
    }
}
