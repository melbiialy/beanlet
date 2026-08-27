package org.study.beanlet.scanner;

import org.study.beanlet.annotation.Component;
import org.study.beanlet.annotation.Configuration;
import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.bean.BeanDefinitionBuilder;
import org.study.beanlet.util.ReflectionUtils;

import java.util.List;

public class ComponentExtractor implements BeanDefinitionExtractor {
    @Override
    public List<BeanDefinition> extract(Class<?> clazz) {
        BeanDefinitionBuilder db = new BeanDefinitionBuilder();
        db.beanClass(clazz);
        db.scope(ReflectionUtils.getBeanScope(clazz));
        db.beanQualifiedName(ReflectionUtils.getBeanQualifiedName(clazz));
        db.lazy(ReflectionUtils.isLazy(clazz));
        db.primary(ReflectionUtils.isPrimary(clazz));
        db.initMethod(ReflectionUtils.getInitMethod(clazz));
        db.destroyMethod(ReflectionUtils.getDestroyMethod(clazz));
        return List.of(db.build());
    }

    @Override
    public boolean support(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class)||clazz.isAnnotationPresent(Configuration.class);
    }
}
