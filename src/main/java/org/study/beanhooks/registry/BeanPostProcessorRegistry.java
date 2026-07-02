package org.study.beanhooks.registry;

import org.study.beanhooks.BeanPostProcessor;

public interface BeanPostProcessorRegistry {
    void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    void removeBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    void clear();
    BeanPostProcessor[] getBeanPostProcessors();
}
