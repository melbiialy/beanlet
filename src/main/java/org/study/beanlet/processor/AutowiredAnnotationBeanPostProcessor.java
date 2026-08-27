package org.study.beanlet.processor;

import org.study.beanlet.factory.BeanFactory;

import java.lang.reflect.Constructor;

public class AutowiredAnnotationBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor, InstantiationAwareBeanPostProcessor, BeanPostProcessor {

    @Override
    public void postProcessProperties(Object bean, String beanName, BeanFactory beanFactory) throws Exception {

    }

    @Override
    public Constructor<?> determineCandidateConstructor(Class<?> beanClass, String beanName) throws Exception {
        return null;
    }
}
