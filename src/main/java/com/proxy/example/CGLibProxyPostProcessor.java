package com.proxy.example;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@SuppressWarnings("unused")
public class CGLibProxyPostProcessor implements BeanPostProcessor {

    private static final Logger logger = Logger.getAnonymousLogger();
    @Autowired
    private ApplicationContext applicationContext;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        boolean containsInOriginal = bean.getClass().isAnnotationPresent(MyAnnotationForCGLib.class); // bean could be not proxied
        // or it could be CGLib proxy, so we have to get original class (let's assume 1 level proxy, otherwise we should iterate by .getSuperClass()
        if (!containsInOriginal)
            containsInOriginal = ClassUtils.getUserClass(bean).isAnnotationPresent(MyAnnotationForCGLib.class);
        // class should not be proxied
        if (!containsInOriginal) return bean;

        logger.log(Level.INFO, "postProcessBeforeInitialization for bean: " + bean.getClass().getName());
        logger.log(Level.INFO, "Is " + MyAnnotationForCGLib.class.getName() + " present? " + bean.getClass().isAnnotationPresent(MyAnnotationForCGLib.class));
        logger.log(Level.INFO, "Making proxy object...");

        return Enhancer.create(bean.getClass(), (MethodInterceptor) (o, method, args, methodProxy) -> {
            try {
                logger.log(Level.INFO, "Before method invocation...");
                return method.invoke(bean, args);
            } finally {
                logger.log(Level.INFO, "After method invocation...");
            }
        });
    }
}
