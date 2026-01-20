package org.study.beanlet.beans.factory;

import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.definition.BeanScope;
import org.study.beanlet.beans.factory.support.*;
import org.study.beanlet.beans.factory.support.scope.Scope;
import org.study.beanlet.beans.factory.support.scope.ScopeRegistry;
import org.study.beanlet.env.PropertySource;

import java.lang.reflect.InvocationTargetException;

public  class DefaultBeanFactory implements BeanFactory {
    private final BeanDefinitionRegistry registry;
    private final ScopeRegistry scopeRegistry;
    private final CreationTracker creationTracker;
    private final BeanCreator beanCreator;
    private final DependencyInjector dependencyInjector;
    private final PropertySource properties;

    public DefaultBeanFactory(BeanDefinitionRegistry registry,PropertySource properties) {
        this.registry = registry;
        this.scopeRegistry = new ScopeRegistry();
        this.creationTracker = new CreationTracker();
        this.beanCreator = new BeanCreator();
        this.dependencyInjector = new DependencyInjector();
        this.properties = properties;
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
        creationTracker.unmarkAsUnderCreated(bean.getClass().getCanonicalName());
        beanDefinition.getInitMethod().invoke(bean);
        scopeRegistry.getScope(beanDefinition.getBeanScope()).register(bean.getClass().getCanonicalName(), bean);

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

}
