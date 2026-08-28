package org.study.beanlet.bean;

import java.lang.reflect.Method;

public class BeanDefinition {
    private String beanQualifiedName;

    public String getBeanQualifiedName() {
        return beanQualifiedName;
    }

    public void setBeanQualifiedName(String beanQualifiedName) {
        this.beanQualifiedName = beanQualifiedName;
    }

    private Class<?> beanClass;
    private BeanScope beanScope;
    private boolean lazy;
    private boolean primary;
    private Method initMethod;
    private Method destroyMethod;
    private Method factoryMethod;
    private String factoryBeanName;


    public BeanDefinition() {
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public BeanScope getBeanScope() {
        return beanScope;
    }

    public void setBeanScope(BeanScope beanScope) {
        this.beanScope = beanScope;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public Method getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    public void setDestroyMethod(Method destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public Method getFactoryMethod() {
        return factoryMethod;
    }

    public void setFactoryMethod(Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }
    public Method getDestroyMethod() {
        return destroyMethod;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
