package com.proxy.example;

/**
 * @Author: Kayson Yang
 * @Date: 2020/3/19 1:32 PM
 * @Desc:
 */
public interface DemoInterface {
    default void method() {
        System.out.println("===== DemoInterface ======");
        //return "FOO";
    }
}
