package com.proxy.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class Main {

    public static void main(String... args) throws IllegalAccessException, InstantiationException {
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



//        Service service = new ByteBuddy()
//                .subclass(Service.class)
//                .method(ElementMatchers.any())
//                .intercept(Advice.to(LoggerAdvisor.class))
//                .make()
//                .load(Service.class.getClassLoader())
//                .getLoaded()
//                .newInstance();
//        service.bar(123);
//        service.foo(456);

        Foo foo = new ByteBuddy()
                .subclass(Foo.class)
                .method(ElementMatchers.any())
                .intercept(Advice.to(LoggerAdvisor.class))
                .make()
                .load(Foo.class.getClassLoader())
                .getLoaded()
                .newInstance();

        String ss = foo.foo2("hello work =====");

        System.out.println(ss);


      /*  Foo dynamicFoo = new ByteBuddy()
                .subclass(Foo.class)
                //匹配由Foo.class声明的方法
                .method(isDeclaredBy(Foo.class)).intercept(FixedValue.value("One!"))
                // 匹配名为foo的方法
                .method(named("foo")).intercept(FixedValue.value("Two!"))
                // 匹配名为foo，入参数量为1的方法
                .method(named("foo").and(takesArguments(1))).intercept(FixedValue.value("Three!"))

                .make()

                .load(Foo.class.getClassLoader())
                .getLoaded()
                .newInstance();
        String res = dynamicFoo.foo("111");
        System.out.println(res);*/
    }


    public static void test() throws Exception {

        Class<? extends UserType> dynamicUserType = new ByteBuddy()
                .subclass(UserType.class)
                .method(ElementMatchers.any())
                .intercept(Advice.to(LoggerAdvisor.class))
                .method(not(isDeclaredBy(Object.class))) // 非父类 Object 声明的方法
                .intercept(MethodDelegation.toField("interceptor")) // 拦截委托给属性字段 interceptor
                .defineField("interceptor", Interceptor.class, Visibility.PRIVATE) // 定义一个属性字段
                .implement(InterceptionAccessor.class)
                .intercept(FieldAccessor.ofBeanProperty()) // 实现 InterceptionAccessor 接口
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
