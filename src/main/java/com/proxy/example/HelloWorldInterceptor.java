package com.proxy.example;

/**
 * @Author: Kayson Yang
 * @Date: 2020/3/19 8:12 PM
 * @Desc:
 */
public  class HelloWorldInterceptor implements Interceptor {
    @Override
    @CustomLog
    public String doSomethingElse() {
        return "====== Hello World! ======";
    }
}
