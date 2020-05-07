package com.home.framework.beans.config;

import java.util.List;

/**
 * @author liqingdong
 * @desc 读取配置信息顶层接口（可扩展为xml，properties，file system等）
 */
public interface LqdBeanDefinitionReader {

    /**
     * 加载配置文件
     * @param locations 本地路径
     * @return
     */
    List<LqdBeanDefinition> loadBeanDefinitions(String... locations);

    /**
     * 获取配置信息
     * @param key 属性key
     * @return 属性值
     */
    String getProperties(String key);
}
