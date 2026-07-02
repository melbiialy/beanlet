package org.study.beanhooks;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{
    Object getEarlyBeanReference(Object bean, String beanName);

}
