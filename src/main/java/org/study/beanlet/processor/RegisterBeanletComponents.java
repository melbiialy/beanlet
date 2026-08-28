package org.study.beanlet.processor;

import org.study.beanlet.applicationevents.ApplicationEventPublisher;
import org.study.beanlet.applicationevents.EventRegistry;
import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.registry.BeanDefinitionRegistry;
import org.study.beanlet.scanner.BeanDefinitionExtractor;
import org.study.beanlet.scanner.ComponentExtractor;

public class RegisterBeanletComponents implements BeanFactoryPostProcessor{
    @Override
    public void postProcessorBeanFactory(BeanDefinitionRegistry registry) {
        BeanDefinitionExtractor beanDefinitionExtractor = new ComponentExtractor();
        BeanDefinition definition =  beanDefinitionExtractor.extract(EventRegistry.class).getFirst();
        registry.registerBeanDefinition(EventRegistry.class.getSimpleName(), definition);
        definition = beanDefinitionExtractor.extract(ApplicationEventPublisher.class).getFirst();
        registry.registerBeanDefinition(ApplicationEventPublisher.class.getSimpleName(), definition);
    }
}
