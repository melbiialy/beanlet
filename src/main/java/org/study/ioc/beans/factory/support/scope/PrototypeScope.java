package org.study.ioc.beans.factory.support.scope;

public class PrototypeScope implements Scope {
    @Override
    public Object get(String beanName) {
        return null;
    }

    @Override
    public Object getEarlyReference(String beanName) {
        return null;
    }

    @Override
    public void register(String beanName, Object bean) {
        // No operation needed for a prototype scope
    }

    @Override
    public void remove(String beanName) {
        // No operation needed for a prototype scope
    }

    @Override
    public void putFactory(String beanName, Object bean) {
        // No operation needed for a prototype scope
    }
}
