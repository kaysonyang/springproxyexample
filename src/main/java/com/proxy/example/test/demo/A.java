package com.proxy.example.test.demo;

/**
 * @Author: Kayson Yang
 * @Date: 2020/4/3 5:41 PM
 * @Desc:
 */
public class A {

    static int a;

    static {
        a = 1;
        b = 1;
    }
    /*static{
        b = b + 1;
    }*/
    static int b;

    public static void main(String[] args) {
        System.out.println(a);
        System.out.println(b);
    }


}
