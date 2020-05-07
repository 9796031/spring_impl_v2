package com.home.framework.web;

import com.home.framework.beans.LqdBeanWrapper;
import com.home.framework.context.support.LqdApplicationContext;
import com.home.framework.stereotype.LQDController;
import com.home.framework.stereotype.LQDRequestMapping;
import com.home.framework.web.servlet.*;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liqingdong
 * @desc spring web容器入口类 <br/>
 * 完成初始化容器, bean初始化, web 9大组件初始化, 处理请求
 */
@Log4j2
public class LqdDispatcherServlet extends HttpServlet {

    private static final String SEPARATOR = "/";
    /** web.xml中配置的key */
    private static final String CONFIG_LOCATION = "configLocation";
    /** 模板配置key (视图)*/
    private static final String TEMPLATE = "template";
    /** applicationContext配置文件路径 (classpath: applicationContext.xml) */
    private static String configLocation;

    /** 处理器映射器 (copy DispatcherServlet) */
    private final List<LqdHandlerMapping> handlerMappings = new ArrayList<>();
    /** 处理器适配器*/
    private final Map<LqdHandlerMapping, LqdHandlerAdaptor> handlerAdaptors = new ConcurrentHashMap<>();
    /** 视图解析器 解析器解析后可以拿到view */
    private final List<LqdViewResolver> viewResolvers = new ArrayList<>();

    /**
     * 初始化
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) {
        configLocation = config.getInitParameter(CONFIG_LOCATION);
        // 初始化组件
        initStrategies();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            // 异常使用modelAndView处理后输出页面
            Map<String, Object> model = new HashMap<>(2);
            model.put("detail", e.getCause().getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()));
            processDispatchResult(req, resp, new LqdModelAndView("500", model));
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 1.通过request中的url匹配到handlerMapping
        LqdHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new LqdModelAndView("404"));
            resp.getWriter().write("404 not found " + req.getRequestURI());
            return;
        }
        // 2. 准备调用参数
        LqdHandlerAdaptor ha = getHandlerAdapter(handler);
        // 3. handlerAdapter调用方法具体method
        LqdModelAndView mv = ha.handle(req, resp, handler);
        // 4. 处理modelAndView, 真正向页面输出结果
        processDispatchResult(req, resp, mv);
    }

    /**
     * 真正处理输出结果
     * @param req 请求对象
     * @param resp 相应对象
     * @param mv 视图&数据
     */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, LqdModelAndView mv) {
        if (mv == null) {return;}
        if (this.viewResolvers.isEmpty()) {return;}
        for (LqdViewResolver viewResolver : viewResolvers) {
            // mv.viewName 假定都为HTTP状态码
            if (viewResolver.getFileName().equals(mv.getViewName())) {
                LqdView view = viewResolver.resolveViewName(mv.getViewName());
                // 根据结果进行渲染
                view.render(mv.getModel(), req, resp);
                return;
            }
        }

    }

    /**
     * 通过handlerMapping获取handlerAdapter
     * @param handler
     * @return
     */
    private LqdHandlerAdaptor getHandlerAdapter(LqdHandlerMapping handler) {
        if (this.handlerAdaptors.isEmpty()) {return null;}
        return this.handlerAdaptors.get(handler);
    }

    /***
     * 通过request获取
     * @param req
     * @return
     */
    private LqdHandlerMapping getHandler(HttpServletRequest req) {
        if (handlerMappings.isEmpty()) {return null;}
        String uri = req.getRequestURI();
        for (LqdHandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(uri);
            // 如果没有匹配上进行下一次匹配
            if (!matcher.matches()) {continue;}
            return handlerMapping;
        }
        return null;
    }

    /**
     * 初始化组件
     */
    private void initStrategies() {
        // 初始化操作
        LqdApplicationContext context = new LqdApplicationContext(configLocation);
        // 文件上传解析器
        initMultipartResolver(context);
        // 国际化解析器
        initLocaleResolver(context);
        // 主题解析器
        initThemeResolver(context);
        // handlerMapping解析器
        initHandlerMappings(context);
        // 初始化参数解析器
        initHandlerAdapters(context);
        // handler异常解析器
        initHandlerExceptionResolvers(context);
        // 初始化视图预处理器
        initRequestToViewNameTranslator(context);
        // 初始化视图转换器
        initViewResolvers(context);
        // 初始化闪存管理
        initFlashMapManager(context);
    }

    private void initFlashMapManager(LqdApplicationContext context) {

    }

    /**
     * 初始化视图解析器, 每个视图都要配置一个解析器,把返回的结果处理后返回给页面
     * @param context
     */
    private void initViewResolvers(LqdApplicationContext context) {
        String templateVal = context.getProperties(TEMPLATE);
        File templatePath = new File(this.getClass().getClassLoader().getResource(templateVal).getFile());
        File[] files = templatePath.listFiles();
        for (File file : files) {
            viewResolvers.add(new LqdViewResolver(file.getPath(), file.getName().replaceAll(".html", "")));
        }
    }

    private void initRequestToViewNameTranslator(LqdApplicationContext context) {

    }

    private void initHandlerExceptionResolvers(LqdApplicationContext context) {

    }

    /***
     * 初始化handlerAdapter, 保存handlerMapping和handlerAdapter关系
     * @param context
     */
    private void initHandlerAdapters(LqdApplicationContext context) {
        // 把一个request变成一个handler,参数都是字符串的, 自动匹配到handler中.
        // handlerAdapter和handlerMapping是一一对应的
        for (LqdHandlerMapping hm : handlerMappings) {
            handlerAdaptors.put(hm, new LqdHandlerAdaptor());
        }
    }

    /**
     * 初始化handlerMapping, 扫秒加载带有RequestMapping注解的method
     * @param context
     */
    private void initHandlerMappings(LqdApplicationContext context) {
        if (handlerMappings.size() != 0) {
            return ;
        }
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            Object bean = context.getBean(beanName);
            if (bean == null) {continue;}
            LqdBeanWrapper beanWrapper = (LqdBeanWrapper) bean;
            Class<?> clazz = beanWrapper.getWrappedClass();
            if (!clazz.isAnnotationPresent(LQDController.class)) {continue;}
            String path = SEPARATOR;
            if (clazz.isAnnotationPresent(LQDRequestMapping.class)) {
                LQDRequestMapping requestMapping = clazz.getAnnotation(LQDRequestMapping.class);
                path = SEPARATOR + requestMapping.value();
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(LQDRequestMapping.class)) {continue;}
                LQDRequestMapping requestMapping = method.getAnnotation(LQDRequestMapping.class);
                String fullPath = path + SEPARATOR + requestMapping.value();
                String url = fullPath.replaceAll("/+", SEPARATOR);
                handlerMappings.add(new LqdHandlerMapping(bean, method, Pattern.compile(url)));
                log.info("loading handler mapping {}", url);
            }
        }
    }

    private void initThemeResolver(LqdApplicationContext context) {

    }

    private void initLocaleResolver(LqdApplicationContext context) {

    }

    private void initMultipartResolver(LqdApplicationContext context) {

    }
}
