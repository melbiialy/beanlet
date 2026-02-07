package org.study.beanlet.core.util;

import org.study.beanlet.annotation.Qualifier;
import org.study.beanlet.beans.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

public class DependencyResolver {

    public static Object[] resolveDependencies(Parameter[] parameters, BeanFactory beanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object[] args = new Object[parameters.length];
        int i = 0;
        for (Parameter parameter : parameters) {
            if (!parameter.getType().isInterface()) {
                args[i++] = beanFactory.getBean(parameter.getType().getName());
            }else {
                String qualifier = null;
                if (parameter.isAnnotationPresent(Qualifier.class)){
                    qualifier = parameter.getAnnotation(Qualifier.class).value();
                }
                args[i++] = beanFactory.getQualifiedBean(parameter.getType().getName(), qualifier);

            }
        }
        return args;

    }
}
