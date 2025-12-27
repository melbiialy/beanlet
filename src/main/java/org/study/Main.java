package org.study;

import org.study.ioc.beans.factory.DefaultBeanFactory;
import org.study.ioc.beans.factory.support.BeanDefinitionRegistry;

import org.study.ioc.componentscan.ComponentScanner;
import org.study.test.service.AuthService;
import org.study.test.service.userService;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ComponentScanner scanner = new ComponentScanner();
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        scanner.scan(registry);
        DefaultBeanFactory beanFactory = new DefaultBeanFactory(registry);
        beanFactory.preInstantiateSingletons();
        AuthService authService = (AuthService) beanFactory.getBean(AuthService.class.getCanonicalName());
        userService userservice = (userService) beanFactory.getBean(userService.class.getCanonicalName());
        System.out.println(authService);
        System.out.println(userservice.authService);
    }
}