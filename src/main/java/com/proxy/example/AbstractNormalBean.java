package com.proxy.example;

import javax.annotation.PostConstruct;

@SuppressWarnings("unused")
public abstract class AbstractNormalBean {
    @PostConstruct
    public void init3(){
        System.out.println("AbstractNormalBean init");
    }
}
