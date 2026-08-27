package org.study.beanlet.scanner;

import org.study.beanlet.registry.BeanDefinitionRegistry;

import java.util.Set;

public interface BeanDefinitionSource {
    void readBeanDefinition(Set<Class<?>> classes, BeanDefinitionRegistry registry);
}
