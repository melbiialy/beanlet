package org.study.beanlet.instantiation;

import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;

public interface BeanCreationStrategy {
    Object create(BeanDefinition beanDefinition, BeanFactory beanFactory) throws Exception;
    boolean support(BeanDefinition beanDefinition);
}
