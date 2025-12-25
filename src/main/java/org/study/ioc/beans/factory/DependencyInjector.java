package org.study.ioc.beans.factory;

import org.study.ioc.beans.defintion.BeanDefinition;

public class DependencyInjector {


    public Object injectDependencies(Object bean, BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) {
        return bean;
    }
}
