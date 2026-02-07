package org.study.beanlet.beans.factory.support.beancreator;

import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.factory.DefaultBeanFactory;
import org.study.beanlet.core.util.ConstructorResolver;
import org.study.beanlet.core.util.DependencyResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BeanInstantiator implements Creator{
    @Override
    public Object create(BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = ConstructorResolver.resolveConstructor(beanDefinition.getBeanClass().getDeclaredConstructors());
        Object[] args = DependencyResolver.resolveDependencies(constructor.getParameters(), defaultBeanFactory);
        return constructor.newInstance(args);
    }

    @Override
    public boolean support(BeanDefinition beanDefinition) {
        return beanDefinition.getFactoryMethod() == null;
    }
}
