package com.proxy.example;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@SuppressWarnings("unused")
public class CGLibProxyPostProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    private static final Logger logger = Logger.getAnonymousLogger();

    private static final HashSet<Class> proxied = new HashSet<>();

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(proxied.contains(bean.getClass().getSuperclass())){
            logger.log(Level.INFO, "postProcessAfterInitialization for bean: " + bean.getClass().getName());
            logger.log(Level.INFO, "Is " + MyAnnotationForCGLib.class.getName() + " present? " + bean.getClass().isAnnotationPresent(MyAnnotationForCGLib.class));

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

        if(!bean.getClass().isAnnotationPresent(MyAnnotationForCGLib.class)) return bean; // annotation will be lost for proxy

        proxied.add(bean.getClass());

        logger.log(Level.INFO, "postProcessBeforeInitialization for bean: " + bean.getClass().getName());
        logger.log(Level.INFO, "Is " + MyAnnotationForCGLib.class.getName() + " present? " + bean.getClass().isAnnotationPresent(MyAnnotationForCGLib.class));
        logger.log(Level.INFO, "Making proxy object...");

        return Enhancer.create(bean.getClass(), (MethodInterceptor)(o, method, args, methodProxy) -> {
            try {
                logger.log(Level.INFO, "Before method invocation...");
                return methodProxy.invokeSuper(o, args);
            } finally {
                logger.log(Level.INFO, "After method invocation...");
            }
        });
    }
}
