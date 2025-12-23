package org.study.ioc.beans.defintion;

public class DependencyDescriptor {
    private Class<?> dependencyType;
    private String qualifier;
    private boolean required;

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
}
