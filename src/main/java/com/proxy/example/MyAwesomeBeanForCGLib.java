package com.proxy.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@MyAnnotationForCGLib
@SuppressWarnings("all")
public class MyAwesomeBeanForCGLib {

    @Autowired
    private NormalBean normalBean;

    public void method(){
        System.out.println("Method was called! " + normalBean.getRandom());
    }
}
