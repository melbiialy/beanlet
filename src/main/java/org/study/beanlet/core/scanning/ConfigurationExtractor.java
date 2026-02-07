package org.study.beanlet.core.scanning;


import org.study.beanlet.annotation.Bean;
import org.study.beanlet.annotation.Configuration;
import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.definition.BeanDefinitionBuilder;
import org.study.beanlet.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

public class ConfigurationExtractor implements BeanDefinitionExtractor {
    @Override
    public List<BeanDefinition> extract(Class<?> clazz) {
        List<Method> methods = ReflectionUtils.getMethods(clazz, Bean.class);
        return methods.stream().map(method->{
            BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
            return builder.beanClass(method.getReturnType())
                    .scope(ReflectionUtils.getBeanScope(method.getReturnType()))
                    .beanQualifiedName(ReflectionUtils.getBeanQualifiedName(method.getReturnType()))
                    .lazy(ReflectionUtils.isLazy(method.getReturnType()))
//                    .primary(ReflectionUtils.isPrimary(method.getReturnType()))
                    .factoryMethod(method)
                    .build();
        }).toList();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return clazz.isAnnotationPresent(Configuration.class);
    }
}
