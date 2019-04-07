package com.proxy.example;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@SuppressWarnings("unused")
@Scope("prototype")
public class NormalBean {

    private int random;

    public NormalBean(){
        System.out.println("Normal bean created!");
        random = ThreadLocalRandom.current().nextInt();
    }

    int getRandom() {
        return random;
    }
}
