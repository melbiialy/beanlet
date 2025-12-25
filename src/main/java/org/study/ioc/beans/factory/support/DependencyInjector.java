package org.study.ioc.beans.factory.support;

import org.study.ioc.beans.defintion.BeanDefinition;
import org.study.ioc.beans.factory.DefaultBeanFactory;

public class DependencyInjector {


    public Object injectDependencies(Object bean, BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) {
        return bean;
    }
}
