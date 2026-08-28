package org.study.beanlet.bean;

import org.study.beanlet.factory.BeanFactory;
import org.study.beanlet.support.DependencyResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
public class BeanWrapper {
    private final BeanDefinition beanDefinition;
    private final BeanFactory beanFactory;
    private final Constructor<?> determineConstructor;

    public BeanWrapper(BeanDefinition beanDefinition, BeanFactory beanFactory, Constructor<?> determineConstructor) {
        this.beanDefinition = beanDefinition;
        this.beanFactory = beanFactory;
        this.determineConstructor = determineConstructor;
    }

    public Object getBean() throws Exception {
        if (beanDefinition.getFactoryMethod() != null) {
            return createViaFactoryMethod();
        }
        return createViaConstructor();
    }

    private Object createViaFactoryMethod() throws Exception {
        Method factoryMethod = beanDefinition.getFactoryMethod();
        Object factoryBean = beanDefinition.getFactoryBeanName() != null
                ? beanFactory.getBean(beanDefinition.getFactoryBeanName())
                : null;
        Object[] args = DependencyResolver.resolveDependencies(factoryMethod.getParameters(), beanFactory);
        factoryMethod.setAccessible(true);
        return factoryMethod.invoke(factoryBean, args);
    }

    private Object createViaConstructor() throws Exception {
        Object[] args = DependencyResolver.resolveDependencies(determineConstructor.getParameters(), beanFactory);
        determineConstructor.setAccessible(true);
        return determineConstructor.newInstance(args);
    }
}
