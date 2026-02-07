package org.study.beanlet.beans.factory.support.beancreator;

import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.factory.DefaultBeanFactory;

import java.lang.reflect.InvocationTargetException;

public interface Creator {
    Object create(BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException;
    boolean support(BeanDefinition beanDefinition);
}
