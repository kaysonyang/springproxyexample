package com.proxy.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String... args){
        AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
        configApplicationContext.scan(Main.class.getPackage().getName());
        configApplicationContext.refresh();

        MyAwesomeBeanForCGLib bean = configApplicationContext.getBean(MyAwesomeBeanForCGLib.class);

        System.out.println("Checking for annotation for CGLIB in main: " + bean.getClass().isAnnotationPresent(MyAnnotationForCGLib.class));

        bean.method();

        MyAwesomeBeanForByteBuddy bean2 = configApplicationContext.getBean(MyAwesomeBeanForByteBuddy.class);

        System.out.println("Checking for annotation for ByteBuddy in main: " + bean2.getClass().isAnnotationPresent(MyAnnotationForByteBuddy.class));

        bean2.method();

        NonExistentBean nonExistentBean = configApplicationContext.getBean(NonExistentBean.class);

        nonExistentBean.method();

        System.out.println(nonExistentBean.hashCode());

        configApplicationContext.close();
    }
}
