package org.study;

import org.study.ioc.beans.factory.DefaultBeanFactory;
import org.study.ioc.beans.factory.support.BeanDefinitionRegistry;

import org.study.ioc.componentscan.ComponentScanner;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ComponentScanner scanner = new ComponentScanner();
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        scanner.scan(registry);
        DefaultBeanFactory beanFactory = new DefaultBeanFactory(registry);
        beanFactory.preInstantiateSingletons();
    }
}