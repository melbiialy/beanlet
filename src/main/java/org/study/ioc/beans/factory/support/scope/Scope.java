package org.study.ioc.beans.factory.support.scope;

public interface Scope {
    Object get(String beanName);
    Object getEarlyReference(String beanName);
    void register(String beanName,Object bean);
    void remove(String beanName);
    void putFactory(String beanName, Object bean);
}
