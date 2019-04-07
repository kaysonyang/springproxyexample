package com.proxy.example;

import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("all")
public interface NonExistentBean {
    public void method();
}
