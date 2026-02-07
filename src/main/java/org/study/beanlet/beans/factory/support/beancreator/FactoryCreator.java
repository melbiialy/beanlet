package org.study.beanlet.beans.factory.support.beancreator;

import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.factory.BeanFactory;
import org.study.beanlet.beans.factory.DefaultBeanFactory;
import org.study.beanlet.core.util.DependencyResolver;
import org.study.beanlet.core.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FactoryCreator implements Creator{

    @Override
    public Object create(BeanDefinition beanDefinition, BeanFactory beanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Method factoryMethod = beanDefinition.getFactoryMethod();
        String className = factoryMethod.getDeclaringClass().getName();
        Object[] args = DependencyResolver.resolveDependencies(factoryMethod.getParameters(), beanFactory);
        factoryMethod.setAccessible(true);
        return factoryMethod.invoke(beanFactory.getBean(className),args);
    }

    @Override
    public boolean support(BeanDefinition beanDefinition) {
        return beanDefinition.getFactoryMethod() != null;
    }
}
