package org.study.beanlet.processor;

import org.study.beanlet.registry.BeanDefinitionRegistry;

public interface BeanFactoryPostProcessor {
    void postProcessorBeanFactory(BeanDefinitionRegistry registry);
}
