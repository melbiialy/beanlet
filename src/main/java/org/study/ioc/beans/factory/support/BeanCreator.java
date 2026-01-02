package org.study.ioc.beans.factory.support;

import org.study.ioc.annotation.Qualifier;
import org.study.ioc.beans.defintion.BeanDefinition;
import org.study.ioc.beans.factory.DefaultBeanFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

public class BeanCreator {
    private final ConstructorResolver constructorResolver;

    public BeanCreator() {
        this.constructorResolver = new ConstructorResolver();
    }

    public Object instantiateBean(String beanName, BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?> constructor = constructorResolver.resolveConstructor(beanClass.getConstructors());
        Parameter [] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        int i = 0;
        for (Parameter parameter : parameters) {
            if (!parameter.getType().isInterface()) {
                args[i++] = defaultBeanFactory.getBean(parameter.getType().getCanonicalName());
            }else {
                String qualifier = null;
                if (parameter.isAnnotationPresent(Qualifier.class)){
                    qualifier = parameter.getAnnotation(Qualifier.class).value();
                }
                args[i++] = defaultBeanFactory.getQualifiedBean(parameter.getType().getCanonicalName(), qualifier);

            }
        }
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }
}
