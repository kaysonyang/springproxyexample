package com.proxy.example;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadLocalRandom;

@Component
@SuppressWarnings("unused")
@Scope("prototype")
public class NormalBean extends AbstractNormalBean{

    @PostConstruct
    public void init(){
        System.out.println("Normal bean init");
    }

    @PostConstruct
    public void init2(){
        System.out.println("Normal bean init2");
    }

    private int random;

    public NormalBean(){
        System.out.println("Normal bean created!");
        random = ThreadLocalRandom.current().nextInt();
    }

    int getRandom() {
        return random;
    }
}
