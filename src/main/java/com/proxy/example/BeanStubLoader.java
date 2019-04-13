package com.proxy.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

@Component
@SuppressWarnings("unused")
public class BeanStubLoader implements BeanDefinitionRegistryPostProcessor {
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        Class<?> target = NonExistentBean.class;

        Class<?> proxyClass = new ByteBuddy()
                .subclass(target)
                .annotateType(target.getDeclaredAnnotations())  // retain parent's annotations
                .method(not(isDeclaredBy(Object.class)))        // Object's class methods will not be proxied
                .intercept(MethodDelegation.to(Interceptor.class))
                .make()
                .load(BeanStubLoader.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();

        registry.registerBeanDefinition(proxyClass.getName(), BeanDefinitionBuilder.genericBeanDefinition(proxyClass).getBeanDefinition());
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    public static class Interceptor {
        @RuntimeType
        public static Object intercept(@Origin Method method, @AllArguments Object... args) {
            try {
                System.out.println("Before method invocation...\n" + method);
                return null;
            } finally {
                System.out.println("After method invocation...");
            }
        }
    }
}
