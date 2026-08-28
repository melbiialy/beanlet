package org.study.beanlet.support;

import org.study.beanlet.annotation.Qualifier;
import org.study.beanlet.annotation.Value;
import org.study.beanlet.factory.BeanFactory;
import org.study.beanlet.env.ValueResolver;

import java.lang.reflect.Parameter;

public class DependencyResolver {


    public static Object[] resolveDependencies(Parameter[] parameters, BeanFactory beanFactory) throws Exception {
        Object[] args = new Object[parameters.length];
        int i = 0;
        for (Parameter parameter : parameters) {
            if(parameter.isAnnotationPresent(Value.class)){
                String path = parameter.getAnnotation(Value.class).value();
                String value = null;
                if (!path.startsWith("${") && !path.endsWith("}")){
                    value = path.trim();
                }else if (path.startsWith("${") && path.endsWith("}")) {
                    value = beanFactory.getValue(path);
                }
                args[i++] = ValueResolver.resolveValue(value, parameter.getType());
                continue;
            }
            if (!parameter.getType().isInterface()) {
                args[i++] = beanFactory.getBean(parameter.getType().getSimpleName());
            }else {
                String qualifier = null;
                if (parameter.isAnnotationPresent(Qualifier.class)){
                    qualifier = parameter.getAnnotation(Qualifier.class).value();
                }
                args[i++] = beanFactory.getBeanByType(parameter.getType(), qualifier);

            }
        }
        return args;

    }
}
