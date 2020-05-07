package com.home.framework.aop.config;

import lombok.Data;

/**
 * @author liqingdong
 * AOP配置信息
 */
@Data
public class LqdAopConfig {

    private String aspectClass;

    private String pointCut;

    private String aspectBefore;

    private String aspectAfter;

    private String aspectAfterThrow;

    private String aspectAfterThrowingName;
}
