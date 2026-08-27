package org.study.beanlet.processor;

public interface BeanPostProcessorRegistry {
    void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    void removeBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    void clear();
    BeanPostProcessor[] getBeanPostProcessors();
}
