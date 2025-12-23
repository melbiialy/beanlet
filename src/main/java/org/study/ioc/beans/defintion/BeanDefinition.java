package org.study.ioc.beans.defintion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class BeanDefinition {
    private String beanName;
    private Class<?> beanClass;
    private BeanScope beanScope;
    private boolean lazy;
    private boolean primary;
    private Method initMethod;
    private Method destroyMethod;
    private Method factoryMethod;
    private Object instance;
    private boolean initialized;
    private Constructor<?> constructorToUse;
    private List<DependencyDescriptor> constructorArgumentDescriptors;
    private List<DependencyDescriptor> propertyDescriptors;
    private List<DependencyDescriptor> methodParameterDescriptors;
    private List<DependencyDescriptor> fieldDependencyDescriptors;

    public BeanDefinition() {
    }

    public BeanDefinition(String beanName, Class<?> beanClass, BeanScope beanScope, boolean lazy, boolean primary, Method initMethod, Method destroyMethod, Method factoryMethod, Object instance, boolean initialized, Constructor<?> constructorToUse, List<DependencyDescriptor> constructorArgumentDescriptors, List<DependencyDescriptor> propertyDescriptors, List<DependencyDescriptor> methodParameterDescriptors, List<DependencyDescriptor> fieldDependencyDescriptors) {
        this.beanName = beanName;
        this.beanClass = beanClass;
        this.beanScope = beanScope;
        this.lazy = lazy;
        this.primary = primary;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
        this.factoryMethod = factoryMethod;
        this.instance = instance;
        this.initialized = initialized;
        this.constructorToUse = constructorToUse;
        this.constructorArgumentDescriptors = constructorArgumentDescriptors;
        this.propertyDescriptors = propertyDescriptors;
        this.methodParameterDescriptors = methodParameterDescriptors;
        this.fieldDependencyDescriptors = fieldDependencyDescriptors;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public BeanScope getScope() {
        return beanScope;
    }

    public void setScope(BeanScope beanScope) {
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

    public Method getDestroyMethod() {
        return destroyMethod;
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

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public Constructor<?> getConstructorToUse() {
        return constructorToUse;
    }

    public void setConstructorToUse(Constructor<?> constructorToUse) {
        this.constructorToUse = constructorToUse;
    }

    public List<DependencyDescriptor> getConstructorArgumentDescriptors() {
        return constructorArgumentDescriptors;
    }

    public void setConstructorArgumentDescriptors(List<DependencyDescriptor> constructorArgumentDescriptors) {
        this.constructorArgumentDescriptors = constructorArgumentDescriptors;
    }

    public List<DependencyDescriptor> getPropertyDescriptors() {
        return propertyDescriptors;
    }

    public void setPropertyDescriptors(List<DependencyDescriptor> propertyDescriptors) {
        this.propertyDescriptors = propertyDescriptors;
    }

    public List<DependencyDescriptor> getMethodParameterDescriptors() {
        return methodParameterDescriptors;
    }

    public void setMethodParameterDescriptors(List<DependencyDescriptor> methodParameterDescriptors) {
        this.methodParameterDescriptors = methodParameterDescriptors;
    }

    public List<DependencyDescriptor> getFieldDependencyDescriptors() {
        return fieldDependencyDescriptors;
    }

    public void setFieldDependencyDescriptors(List<DependencyDescriptor> fieldDependencyDescriptors) {
        this.fieldDependencyDescriptors = fieldDependencyDescriptors;
    }
    public boolean isPrototype(){
        return beanScope == BeanScope.PROTOTYPE;
    }
    public boolean isSingleton(){
        return beanScope == BeanScope.SINGLETON;
    }
    public boolean isFactoryBean(){
        return factoryMethod != null;
    }
    public boolean hasDependencies(){
        return !propertyDescriptors.isEmpty() || !methodParameterDescriptors.isEmpty();
    }
    public List<DependencyDescriptor> getAllDependencyDescriptors(){
        List<DependencyDescriptor> all = new java.util.ArrayList<>();
        all.addAll(constructorArgumentDescriptors);
        all.addAll(propertyDescriptors);
        all.addAll(methodParameterDescriptors);
        return all;
    }
}
