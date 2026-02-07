package org.study.beanlet.beans.factory.support.beancreator;

import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CreatorRegistry {
    private final List<Creator> creators;

    public CreatorRegistry(List<Creator> creators) {
        this.creators = creators;
    }
    public Object createBean(BeanDefinition beanDefinition, BeanFactory beanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return creators.stream()
                .filter(creator -> creator.support(beanDefinition))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No suitable creator found for bean: " + beanDefinition.getBeanClass().getName()))
                .create(beanDefinition, beanFactory);
    }
}
