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
        if (creationTracker.isUnderCreated(beanName)) {
            throw new RuntimeException("Circular dependency detected");
        }
        if (beanDefinition.isSingleton()) {
            Object singletonBean = singletonBeanRegistry.getSingleton(beanName);
            if (singletonBean != null) {
                return singletonBean;
            }
            else if (singletonBeanRegistry.containsEarlyBean(beanName)){
               return singletonBeanRegistry.getEarlyBean(beanName);
            }
            else if (singletonBeanRegistry.containsFactoryBean(beanName)){
                return singletonBeanRegistry.getBeanFromFactory(beanName);
            }
        }

        creationTracker.trackCreation(beanName);
        Object bean = beanCreator.createBean(beanName, beanDefinition, this);
        creationTracker.stopTracking(beanName);
        Object finalBean = bean;
        singletonBeanRegistry.addFactoryBean(beanName, finalBean);
        bean = dependencyInjector.injectDependencies(bean, beanDefinition, this);

        if (beanDefinition.isSingleton()) {
            if (singletonBeanRegistry.containsEarlyBean(beanName)){
                singletonBeanRegistry.getEarlyBean(beanName);
            }
            if (singletonBeanRegistry.containsFactoryBean(beanName)){
                singletonBeanRegistry.getBeanFromFactory(beanName);
            }
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
