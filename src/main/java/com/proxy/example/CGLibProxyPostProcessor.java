package com.proxy.example;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@SuppressWarnings("unused")
public class CGLibProxyPostProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    private static final Logger logger = Logger.getAnonymousLogger();

    private final HashSet<Class> shouldBeProxied = new HashSet<>();

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(MyAnnotationForCGLib.class)) shouldBeProxied.add(bean.getClass());
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        boolean containsInOriginal = shouldBeProxied.contains(bean.getClass()); // bean could be not proxied
        // or it could be CGLib proxy, so we have to get original class
        if(!containsInOriginal) containsInOriginal = shouldBeProxied.contains(ClassUtils.getUserClass(bean));
        // class should not be proxied
        if(!containsInOriginal) return bean;

        logger.log(Level.INFO, "postProcessBeforeInitialization for bean: " + bean.getClass().getName());
        logger.log(Level.INFO, "Is " + MyAnnotationForCGLib.class.getName() + " present? " + bean.getClass().isAnnotationPresent(MyAnnotationForCGLib.class));
        logger.log(Level.INFO, "Making proxy object...");

        return Enhancer.create(bean.getClass(), (MethodInterceptor)(o, method, args, methodProxy) -> {
            try {
                logger.log(Level.INFO, "Before method invocation...");
                return method.invoke(bean, args);
            } finally {
                logger.log(Level.INFO, "After method invocation...");
            }
        });
    }
}
