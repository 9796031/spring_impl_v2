package com.home.framework.stereotype;

import java.lang.annotation.*;

/**
 * @author liqingdong
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LQDRequestParam {

	String value() default "";
}