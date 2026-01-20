package org.study.ioc.beans.factory;

import org.study.ioc.beans.defintion.BeanDefinition;
import org.study.ioc.beans.defintion.BeanScope;
import org.study.ioc.beans.factory.support.*;
import org.study.ioc.beans.factory.support.scope.Scope;
import org.study.ioc.beans.factory.support.scope.ScopeRegistry;

import java.lang.reflect.InvocationTargetException;

public  class DefaultBeanFactory implements BeanFactory {
    private final BeanDefinitionRegistry registry;
    private final ScopeRegistry scopeRegistry;
    private final CreationTracker creationTracker;
    private final BeanCreator beanCreator;
    private final DependencyInjector dependencyInjector;

    public DefaultBeanFactory(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.scopeRegistry = new ScopeRegistry();
        this.creationTracker = new CreationTracker();
        this.beanCreator = new BeanCreator();
        this.dependencyInjector = new DependencyInjector();
    }

    @Override
    public Object getBean(String beanName) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
        if (beanDefinition == null) {
            throw new RuntimeException("No bean found for name: " + beanName);
        }

        Object bean = doGetBean(beanName,beanDefinition.getBeanScope());

        if (bean != null) {
            return bean;
        }
        bean = createBean(beanName, beanDefinition);
        populateBean(bean,beanDefinition);

        return bean;
    }

    private void populateBean(Object bean, BeanDefinition beanDefinition) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        dependencyInjector.fieldsInjection(bean,beanDefinition,this);
        dependencyInjector.methodsInjection(bean,beanDefinition,this);
        scopeRegistry.getScope(beanDefinition.getBeanScope()).register(beanDefinition.getBeanQualifiedName(), bean);

    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        creationTracker.markAsUnderCreated(beanName);
        Object bean = beanCreator.instantiateBean(beanDefinition,this);
        scopeRegistry.getScope(beanDefinition.getBeanScope()).putFactory(beanName, bean);
        return bean;
    }

    private Object doGetBean(String beanName, BeanScope beanScope) {
        Scope scope = scopeRegistry.getScope(beanScope);
        Object bean = scope.get(beanName);
        if (bean != null) {
            return bean;
        }
        if (creationTracker.isUnderCreated(beanName)) {
            bean = scope.getEarlyReference(beanName);
            if (bean != null) {
                return bean;
            }
            ErrorLogger.reportError(creationTracker.getNames(), beanName);
        }
        return null;
    }

    @Override
    public Object getQualifiedBean(String beanName, String value) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        String qualifiedBeanName = registry.getTypeMatchBeanDefinition(beanName,value);
        return getBean(qualifiedBeanName);
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
