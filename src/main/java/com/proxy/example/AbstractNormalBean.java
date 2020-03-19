package com.proxy.example;

import javax.annotation.PostConstruct;

@SuppressWarnings("unused")
public abstract class AbstractNormalBean {

    public AbstractNormalBean() {
        System.out.println("AbstractNormalBean bean created!");
    }

    @PostConstruct
    public void init3() {
        System.out.println("AbstractNormalBean init");
    }
}
