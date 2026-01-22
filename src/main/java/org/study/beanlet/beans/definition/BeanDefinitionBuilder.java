package org.study.beanlet.beans.definition;

import java.lang.reflect.Method;

public class BeanDefinitionBuilder {
    private BeanDefinition beanDefinition;
    public BeanDefinitionBuilder() {
        this.beanDefinition = new BeanDefinition();
    }
    public BeanDefinitionBuilder beanClass(Class<?> beanClass) {
        beanDefinition.setBeanClass(beanClass);
        return this;
    }
    public BeanDefinitionBuilder scope(BeanScope scope) {
        beanDefinition.setBeanScope(scope);
        return this;
    }
    public BeanDefinitionBuilder lazy(boolean lazy) {
        beanDefinition.setLazy(lazy);
        return this;
    }
    public BeanDefinitionBuilder primary(boolean primary) {
        beanDefinition.setPrimary(primary);
        return this;
    }
    public BeanDefinitionBuilder beanQualifiedName(String name) {
        beanDefinition.setBeanQualifiedName(name);
        return this;
    }
    public BeanDefinitionBuilder initMethod(Method initMethod) {
        beanDefinition.setInitMethod(initMethod);
        return this;
    }
    public BeanDefinitionBuilder destroyMethod(Method destroyMethod) {
        beanDefinition.setDestroyMethod(destroyMethod);
        return this;
    }


    public BeanDefinition build() {
        return beanDefinition;
    }
}
