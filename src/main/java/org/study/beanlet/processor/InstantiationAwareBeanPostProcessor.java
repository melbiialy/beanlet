package org.study.beanlet.processor;

import org.study.beanlet.factory.BeanFactory;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    void postProcessProperties(Object bean, String beanName, BeanFactory beanFactory) throws Exception;

}
