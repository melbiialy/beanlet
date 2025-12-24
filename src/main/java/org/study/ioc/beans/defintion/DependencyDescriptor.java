package org.study.ioc.beans.defintion;


import java.lang.reflect.Type;

public class DependencyDescriptor {
    private String beanName;
    private Type dependencyType;
    private String qualifier;
    private boolean required;


}
