package org.study.beanlet.scanner;

import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.registry.BeanDefinitionRegistry;

import java.util.List;
import java.util.Set;

public class BeanDefinitionReader implements BeanDefinitionSource {
    private final List<BeanDefinitionExtractor> extractors;

    public BeanDefinitionReader(List<BeanDefinitionExtractor> extractors) {
        this.extractors = extractors;
    }

    @Override
    public void readBeanDefinition(Set<Class<?>> classes, BeanDefinitionRegistry registry){
        for (Class<?> clazz : classes) {
            for (BeanDefinitionExtractor extractor : extractors) {
                if (extractor.support(clazz)){
                    List<BeanDefinition> beanDefinitions = extractor.extract(clazz);
                    beanDefinitions
                            .forEach(beanDefinition -> {
                                registry.registerBeanDefinition(beanDefinition.getBeanClass().getName(), beanDefinition);});
                }
            }
        }
    }


}
