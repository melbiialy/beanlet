package org.study.beanlet.instantiation;

import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class BeanCreationStrategyResolver {
    private final List<BeanCreationStrategy> creators;

    public BeanCreationStrategyResolver(List<BeanCreationStrategy> creators) {
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
