package org.study.ioc.beans.factory.support.scope;

public class SingletonScope implements Scope {
    private final SingletonBeanRegistry singletonBeanRegistry;
    public SingletonScope(SingletonBeanRegistry singletonBeanRegistry) {
        this.singletonBeanRegistry = singletonBeanRegistry;
    }
    @Override
    public Object get(String beanName) {
        return singletonBeanRegistry.getSingleton(beanName,false);
    }

    @Override
    public Object getEarlyReference(String beanName) {
        return singletonBeanRegistry.getSingleton(beanName,true);
    }

    @Override
    public void register(String beanName, Object bean) {
        singletonBeanRegistry.registerSingleton(beanName,bean);
    }

    @Override
    public void remove(String beanName) {
        // No operation needed for singleton scope
    }

    @Override
    public void putFactory(String beanName, Object bean) {
        singletonBeanRegistry.registerSingletonFactory(beanName,()->bean);
    }
}
