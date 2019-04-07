package com.proxy.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.bytebuddy.matcher.ElementMatchers.any;

@Component
@SuppressWarnings("unused")
public class ByteBuddyPostProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    private static final Logger logger = Logger.getAnonymousLogger();

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException { return bean; }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if(!bean.getClass().isAnnotationPresent(MyAnnotationForByteBuddy.class)) return bean;

        logger.log(Level.INFO, "postProcessBeforeInitialization for bean: " + bean.getClass().getName());
        logger.log(Level.INFO, "Is " + MyAnnotationForByteBuddy.class.getName() + " present? " + bean.getClass().isAnnotationPresent(MyAnnotationForByteBuddy.class));
        logger.log(Level.INFO, "Making proxy object...");

        Class<?> proxyClass = new ByteBuddy()
                .subclass(bean.getClass())
                .defineField("target", bean.getClass(), Visibility.PRIVATE)
                .annotateType(bean.getClass().getDeclaredAnnotations()) // retain parent's annotations
                .method(any())
                .intercept(MethodDelegation.to(Interceptor.class))
                .make()
                .load(bean.getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();

        Object beanInstance = null;
        try{
            beanInstance = proxyClass.newInstance();
            Field target = beanInstance.getClass().getDeclaredField("target");
            target.setAccessible(true);
            target.set(beanInstance, bean);
        }catch(Exception e){
            e.printStackTrace();
        }

        return beanInstance;
    }

    public static class Interceptor {

        @RuntimeType
        public static Object intercept(@SuperCall Callable<?> superCall, @SuperMethod Method superMethod, @Origin Method currentMethod,
                                       @AllArguments Object[] args, @This(optional = true) Object me) throws Exception {
            try {
                logger.log(Level.INFO, "Before method invocation...");
                Field target = me.getClass().getDeclaredField("target");
                target.setAccessible(true);
                return currentMethod.invoke(target.get(me), args);
            } finally {
                logger.log(Level.INFO, "After method invocation...");
            }
        }
    }
}
