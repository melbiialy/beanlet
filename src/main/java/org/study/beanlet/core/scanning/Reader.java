package org.study.beanlet.core.scanning;

import org.study.beanlet.beans.factory.support.BeanDefinitionRegistry;

import java.util.Set;

public interface Reader {
    void readBeanDefinition(Set<Class<?>> classes, BeanDefinitionRegistry registry);
}
