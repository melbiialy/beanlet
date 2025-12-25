package org.study;

import org.study.ioc.beans.factory.DefaultBeanFactory;
import org.study.ioc.beans.factory.support.BeanDefinitionRegistry;

import org.study.ioc.componentscan.ComponentScanner;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        ComponentScanner scanner = new ComponentScanner();
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        scanner.scan(registry);
        DefaultBeanFactory beanFactory = new DefaultBeanFactory(registry);
        beanFactory.preInstantiateSingletons();




    }
}