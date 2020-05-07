package com.home.framework.stereotype;

import java.lang.annotation.*;

/**
 * @author liqingdong
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LQDRequestMapping {
        String value() default "";
}
