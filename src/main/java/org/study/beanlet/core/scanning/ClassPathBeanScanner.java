package org.study.beanlet.core.scanning;


import org.study.beanlet.beans.factory.support.BeanDefinitionRegistry;
import org.study.beanlet.env.PropertySource;

import java.util.HashSet;
import java.util.Set;


public class ClassPathBeanScanner implements BeanScanner {
    private final ClassPathScanner classScanner;
    private final BeanDefinitionSource beanDefinitionReader;
    private String basePackage;
    private static final String DEFAULT_BASE_PACKAGE = "";


    public ClassPathBeanScanner(PropertySource propertySource, ClassPathScanner loader, BeanDefinitionSource reader) {
        initPackages(propertySource);
        this.classScanner = loader;
        this.beanDefinitionReader = reader;
    }

    private void initPackages(PropertySource propertySource) {
        if (propertySource.getProperty("beanlet.scan.base-package") != null) {
            basePackage = propertySource.getProperty("beanlet.scan.base-package");
        } else {
            basePackage = DEFAULT_BASE_PACKAGE;
        }
    }

    @Override
    public void scan(BeanDefinitionRegistry registry) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        classScanner.loadClasses(basePackage, classes);
        beanDefinitionReader.readBeanDefinition(classes, registry);
    }

}

