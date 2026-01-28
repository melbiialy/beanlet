package org.study.beanlet.core.scanning;

import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.factory.support.BeanDefinitionRegistry;
import org.study.beanlet.core.util.ReflectionUtils;

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
                                registry.registerBeanDefinition(clazz.getName(), beanDefinition);
                                handleTypeCache(clazz,registry);});
                }
            }
        }
    }

    private void handleTypeCache(Class<?> clazz, BeanDefinitionRegistry registry) {
        String [] interfaces = ReflectionUtils.getAllInterfaces(clazz);
        for (String interfaceName : interfaces) {
            registry.addTypeInjectionCache(interfaceName, clazz.getName());
        }
    }
}
