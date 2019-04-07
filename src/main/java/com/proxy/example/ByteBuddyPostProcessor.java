package com.proxy.example;

import net.bytebuddy.ByteBuddy;
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
import java.util.HashSet;
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

    private static final HashSet<Class> proxied = new HashSet<>();

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(proxied.contains(bean.getClass().getSuperclass())){
            logger.log(Level.INFO, "postProcessAfterInitialization for bean: " + bean.getClass().getName());
            logger.log(Level.INFO, "Is " + MyAnnotationForByteBuddy.class.getName() + " present? " + bean.getClass().isAnnotationPresent(MyAnnotationForByteBuddy.class));

            Field[] fields = bean.getClass().getSuperclass().getDeclaredFields();
            for(Field field : fields){
                if(field.getAnnotation(Autowired.class) != null){
                    try {
                        field.setAccessible(true);
                        field.set(bean, applicationContext.getAutowireCapableBeanFactory().getBean(field.getType()));
                    }catch (IllegalAccessException e){
                        logger.log(Level.SEVERE, e.getMessage());
                    }
                }
            }
        }
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if(!bean.getClass().isAnnotationPresent(MyAnnotationForByteBuddy.class)) return bean;

        proxied.add(bean.getClass());

        logger.log(Level.INFO, "postProcessBeforeInitialization for bean: " + bean.getClass().getName());
        logger.log(Level.INFO, "Is " + MyAnnotationForByteBuddy.class.getName() + " present? " + bean.getClass().isAnnotationPresent(MyAnnotationForByteBuddy.class));
        logger.log(Level.INFO, "Making proxy object...");

        Class<?> proxyClass = new ByteBuddy()
                .subclass(bean.getClass())
                .annotateType(bean.getClass().getDeclaredAnnotations()) // retain parent's annotations
                .method(any())
                .intercept(MethodDelegation.to(Interceptor.class))
                .make()
                .load(bean.getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();

        Object beanInstance = null;
        try{
            beanInstance = proxyClass.newInstance();
        }catch(InstantiationException | IllegalAccessException e){
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
                return superCall.call();
            } finally {
                logger.log(Level.INFO, "After method invocation...");
            }
        }
    }
}
