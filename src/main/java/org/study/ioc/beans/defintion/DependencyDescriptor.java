package org.study.ioc.beans.defintion;

import java.lang.reflect.Method;

public class DependencyDescriptor {
    private String beanName;
    private Class<?> dependencyType;
    private String qualifier;
    private boolean required;
    private Method setterMethod;

    public DependencyDescriptor(Class<?> dependencyType, String qualifier, boolean required) {
        this.dependencyType = dependencyType;
        this.qualifier = qualifier;
        this.required = required;
    }

    public DependencyDescriptor() {
    }

    public Class<?> getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(Class<?> dependencyType) {
        this.dependencyType = dependencyType;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Method getSetterMethod() {
        return setterMethod;
    }

    public void setSetterMethod(Method setterMethod) {
        this.setterMethod = setterMethod;
    }
}
