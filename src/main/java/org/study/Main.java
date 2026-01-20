package org.study;

import org.study.ioc.beans.factory.DefaultBeanFactory;
import org.study.ioc.beans.factory.support.BeanDefinitionRegistry;

import org.study.ioc.componentscan.ComponentScanner;
import org.study.ioc.context.ApplicationContext;
import org.study.ioc.context.DefaultApplicationContext;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {

        ApplicationContext applicationContext = new DefaultApplicationContext();
        applicationContext.refresh();

    }
}