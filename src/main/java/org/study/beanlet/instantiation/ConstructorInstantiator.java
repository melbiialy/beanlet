package org.study.beanlet.instantiation;

import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.factory.BeanFactory;
import org.study.beanlet.support.ConstructorResolver;
import org.study.beanlet.support.DependencyResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstructorInstantiator implements BeanCreationStrategy{
    @Override
    public Object create(BeanDefinition beanDefinition, BeanFactory beanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = ConstructorResolver.resolveConstructor(beanDefinition.getBeanClass().getDeclaredConstructors());
        Object[] args = DependencyResolver.resolveDependencies(constructor.getParameters(), beanFactory);
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

    @Override
    public boolean support(BeanDefinition beanDefinition) {
        return beanDefinition.getFactoryMethod() == null;
    }
}
