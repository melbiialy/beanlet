package org.study.beanlet.instantiation;

import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.factory.BeanFactory;
import org.study.beanlet.support.DependencyResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FactoryMethodInstantiator implements BeanCreationStrategy{

    @Override
    public Object create(BeanDefinition beanDefinition, BeanFactory beanFactory) throws Exception {
        Method factoryMethod = beanDefinition.getFactoryMethod();
        String className = factoryMethod.getDeclaringClass().getSimpleName();
        Object[] args = DependencyResolver.resolveDependencies(factoryMethod.getParameters(), beanFactory);
        factoryMethod.setAccessible(true);
        return factoryMethod.invoke(beanFactory.getBean(className),args);
    }

    @Override
    public boolean support(BeanDefinition beanDefinition) {
        return beanDefinition.getFactoryMethod() != null;
    }
}
