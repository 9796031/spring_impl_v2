package com.home.test;

import com.home.framework.context.support.LqdApplicationContext;

public class ApplicationContextTest {

    public static void main(String[] args) {
        LqdApplicationContext ac = new LqdApplicationContext("applicationContext.properties");
        System.out.println(ac);
    }
}
