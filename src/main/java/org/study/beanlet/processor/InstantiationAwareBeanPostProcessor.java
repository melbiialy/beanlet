package org.study.beanlet.processor;

import org.study.beanlet.factory.BeanFactory;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    default Object getEarlyBeanReference(Object bean, String beanName){
        return null;
    }
    void postProcessProperties(Object bean, String beanName, BeanFactory beanFactory) throws Exception;

}
