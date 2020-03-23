package com.proxy.example;

/**
 * @Author: Kayson Yang
 * @Date: 2020/3/23 4:36 PM
 * @Desc:
 */
public class Foo {


    public String bar() {
        return "bar";
    }


    public String foo() {
        return "foo";
    }

    @CustomLog
    public String foo2(String s) {
        return "foo " + s;
    }
}
