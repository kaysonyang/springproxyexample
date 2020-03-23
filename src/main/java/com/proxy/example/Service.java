package com.proxy.example;

/**
 * @Author: Kayson Yang
 * @Date: 2020/3/23 4:06 PM
 * @Desc:
 */
public class Service {
    @CustomLog
    public int foo(int value) {
        System.out.println("foo: " + value);
        return value;
    }

    public int bar(int value) {
        System.out.println("bar: " + value);
        return value;
    }
}

