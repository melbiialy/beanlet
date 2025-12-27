package org.study.ioc.beans.factory;

import org.study.ioc.beans.defintion.BeanDefinition;
import org.study.ioc.beans.factory.support.*;

import java.lang.reflect.InvocationTargetException;

public  class DefaultBeanFactory implements BeanFactory {
    private final BeanDefinitionRegistry registry;
    private final SingletonBeanRegistry singletonBeanRegistry;
    private final CreationTracker creationTracker;
    private final BeanCreator beanCreator;
    private final DependencyInjector dependencyInjector;

    public DefaultBeanFactory(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.singletonBeanRegistry = new SingletonBeanRegistry();
        this.creationTracker = new CreationTracker();
        this.beanCreator = new BeanCreator();
        this.dependencyInjector = new DependencyInjector();
    }

    @Override
    public Object getBean(String beanName) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
        if (beanDefinition == null) {
            throw new RuntimeException("No such bean definition: " + beanName);
        }
        if (beanDefinition.isSingleton()){
            Object bean = singletonBeanRegistry.getSingleton(beanName,false);
            if (bean != null){
                return bean;
            }
        }
        if (creationTracker.isUnderCreated(beanName)) {
            if (beanDefinition.isSingleton()) {
                Object singletonBean = singletonBeanRegistry.getSingleton(beanName,true);
                if (singletonBean != null) {
                    return singletonBean;
                }
            }
            ErrorLogger.reportError(creationTracker.getNames(),beanName);
        }
        creationTracker.markAsUnderCreated(beanName);
        Object bean = beanCreator.instantiateBean(beanName, beanDefinition, this);
        if (beanDefinition.isSingleton()){
            singletonBeanRegistry.registerSingletonFactory(beanName,()->bean);
        }
        dependencyInjector.injectDependencies(bean, beanDefinition, this);
        creationTracker.unmarkAsUnderCreated(beanName);

        if (beanDefinition.isSingleton()) {
            singletonBeanRegistry.registerSingleton(beanName, bean);
        }
        return bean;
    }
    public void preInstantiateSingletons() {
        for (String beanName : registry.getBeanNames()) {
            try {
                getBean(beanName);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
