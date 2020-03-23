package com.proxy.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

public class Main {

    public static void main(String... args) {
        AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
        configApplicationContext.scan(Main.class.getPackage().getName());
        configApplicationContext.refresh();

        MyAwesomeBeanForCGLib bean = configApplicationContext.getBean(MyAwesomeBeanForCGLib.class);

        System.out.println("Checking for annotation for CGLIB in main: " + bean.getClass().isAnnotationPresent(MyAnnotationForCGLib.class)); // false for CGLib

        bean.method();

        MyAwesomeBeanForByteBuddy bean2 = configApplicationContext.getBean(MyAwesomeBeanForByteBuddy.class);

        System.out.println("Checking for annotation for ByteBuddy in main: " + bean2.getClass().isAnnotationPresent(MyAnnotationForByteBuddy.class)); // true for ByteBuddy

        bean2.method();

        NonExistentBean nonExistentBean = configApplicationContext.getBean(NonExistentBean.class);

        nonExistentBean.method(); // proxied

        System.out.println(nonExistentBean.hashCode()); // not proxied

        configApplicationContext.close();


        try {
            test();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void test() throws Exception {

        Class<? extends UserType> dynamicUserType = new ByteBuddy()
                .subclass(UserType.class)
                .method(not(isDeclaredBy(Object.class))) // 非父类 Object 声明的方法
                .intercept(MethodDelegation.toField("interceptor")) // 拦截委托给属性字段 interceptor
                .defineField("interceptor", Interceptor.class, Visibility.PRIVATE) // 定义一个属性字段
                .implement(InterceptionAccessor.class).intercept(FieldAccessor.ofBeanProperty()) // 实现 InterceptionAccessor 接口
                .make()
                .load(Main.class.getClassLoader())
                .getLoaded();

//        InstanceCreator factory = new ByteBuddy()
//                .subclass(InstanceCreator.class)
//                .method(not(isDeclaredBy(Object.class))) // 非父类 Object 声明的方法
//                .intercept(MethodDelegation.toConstructor(dynamicUserType)) // 委托拦截的方法来调用提供的类型的构造函数
//                .make()
//                .load(dynamicUserType.getClassLoader())
//                .getLoaded().newInstance();

        //UserType userType = (UserType) factory.makeInstance();
        UserType userType = dynamicUserType.newInstance();
        ((InterceptionAccessor) userType).setInterceptor(new HelloWorldInterceptor());
        String s = userType.doSomething();
        System.out.println(s); // Hello World!
    }




}
