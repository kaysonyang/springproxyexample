package com.proxy.example;

/**
 * @Author: Kayson Yang
 * @Date: 2020/3/19 8:01 PM
 * @Desc:
 */
public interface InterceptionAccessor {
    Interceptor getInterceptor();
    void setInterceptor(Interceptor interceptor);
}
