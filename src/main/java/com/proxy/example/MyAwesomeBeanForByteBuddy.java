package com.proxy.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@MyAnnotationForByteBuddy
@SuppressWarnings("all")
public class MyAwesomeBeanForByteBuddy {

    @Autowired
    private NormalBean normalBean;

    public void method() {
        System.out.println("Method was called! " + normalBean.getRandom());
    }
}
