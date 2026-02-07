package org.study.beanlet.beans.factory.support;

import org.study.beanlet.annotation.Qualifier;
import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.factory.DefaultBeanFactory;
import org.study.beanlet.core.util.ConstructorResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

public class BeanCreator {
    private final ConstructorResolver constructorResolver;

    public BeanCreator() {
        this.constructorResolver = new ConstructorResolver();
    }

    public Object instantiateBean(BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?> constructor = constructorResolver.resolveConstructor(beanClass.getConstructors());
        Parameter [] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        int i = 0;
        for (Parameter parameter : parameters) {
            if (!parameter.getType().isInterface()) {
                args[i++] = defaultBeanFactory.getBean(parameter.getType().getName());
            }else {
                String qualifier = null;
                if (parameter.isAnnotationPresent(Qualifier.class)){
                    qualifier = parameter.getAnnotation(Qualifier.class).value();
                }
                args[i++] = defaultBeanFactory.getQualifiedBean(parameter.getType().getName(), qualifier);

            }
        }
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }
}
