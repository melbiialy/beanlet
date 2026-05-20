package org.study.beanlet.beans.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.definition.BeanScope;
import org.study.beanlet.beans.factory.support.*;
import org.study.beanlet.beans.factory.support.beancreator.CreatorRegistry;
import org.study.beanlet.beans.factory.support.beanregistry.BeanCacheManager;
import org.study.beanlet.logging.ErrorLogger;
import org.study.beanlet.env.PropertySource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public  class DefaultBeanFactory implements BeanFactory, AutoCloseable {
    private final BeanDefinitionRegistry registry;
    private final CreationTracker creationTracker;
    private final CreatorRegistry creatorRegistry;
    private final DependencyInjector dependencyInjector;
    private final Logger logger =  LoggerFactory.getLogger(DefaultBeanFactory.class);
    private final BeanCacheManager beanCacheManager;
    private final ThreadLocal<Boolean> allowEarlyReference;
    private final PropertySource properties;

    public DefaultBeanFactory(BeanDefinitionRegistry registry, PropertySource properties, BeanCacheManager beanCacheManager, CreatorRegistry creatorRegistry) {
        this.registry = registry;
        this.creationTracker = new CreationTracker();
        this.creatorRegistry = creatorRegistry;
        this.dependencyInjector = new DependencyInjector();
        this.beanCacheManager = beanCacheManager;
        allowEarlyReference = ThreadLocal.withInitial(() -> false);
        this.properties = properties;
    }

    @Override
    public Object getBean(String beanName) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        logger.trace("Getting bean: {}", beanName);
        BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
        if (beanDefinition == null) {
            throw new RuntimeException("No bean found for name: " + beanName);
        }

        Object bean = doGetBean(beanName,beanDefinition.getBeanScope());

        if (bean != null) {
            return bean;
        }
        bean = createBean(beanName, beanDefinition);
        populateBean(beanName, bean, beanDefinition);

        return bean;
    }

    private void populateBean(String beanName, Object bean, BeanDefinition beanDefinition) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        logger.trace("Populating bean: {}", beanName);
        allowEarlyReference.set(true);
        dependencyInjector.fieldsInjection(bean,beanDefinition,this);
        dependencyInjector.methodsInjection(bean,beanDefinition,this);
        creationTracker.unmarkAsUnderCreated(beanName);
        logger.trace("Bean {} fully initialized.", beanName);
        if (beanDefinition.getInitMethod() != null) {
            beanDefinition.getInitMethod().invoke(bean);
        }
        beanCacheManager.registerBean(beanName,beanDefinition.getBeanScope(),bean);
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        allowEarlyReference.set(false);
        logger.trace("Creating bean: {}", beanName);
        creationTracker.markAsUnderCreated(beanName);
        Object bean = creatorRegistry.createBean(beanDefinition,this);
        beanCacheManager.registerEarlyFactoryBean(beanName,bean,beanDefinition.getBeanScope());
        return bean;
    }

    private Object doGetBean(String beanName, BeanScope beanScope) {
        Object bean = beanCacheManager.getBean(beanName, allowEarlyReference.get(),beanScope);
        if (bean != null) {
            logger.trace("Bean {} found in scope {}", beanName, beanScope);
            return bean;
        }
        if (creationTracker.isUnderCreated(beanName)) {
            logger.trace("Bean {} is still being created", beanName);
            logger.trace("Getting early reference for bean {}: {}", beanName, null);
            logger.error("Circular dependency detected for bean: {}", beanName);
            ErrorLogger.reportError(creationTracker.getNames(), beanName);
        }
        return null;
    }

    @Override
    public Object getQualifiedBean(String beanName, String value) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        logger.trace("Getting qualified bean: {} with qualifier: {}", beanName, value);
        String qualifiedBeanName = registry.getTypeMatchBeanDefinition(beanName,value);
        return getBean(qualifiedBeanName);
    }

    @Override
    public String getValue(String path) {
        return properties.getProperty(path);
    }

    @Override
    public void close() throws IOException {
        logger.info("Shutting down BeanFactory, destroying singleton beans...");
        for (String beanName : registry.getBeanNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (beanDefinition.getBeanScope() != BeanScope.SINGLETON){
                continue;
            }
            if (beanDefinition.getDestroyMethod() == null){
                continue;
            }
            Object bean = beanCacheManager.getBean(beanName,false,beanDefinition.getBeanScope());
            if (bean == null)continue;
            try {
                beanDefinition.getDestroyMethod().invoke(bean);
                logger.trace("Destroy method invoked on bean: {}", beanName);
            } catch (InvocationTargetException | IllegalAccessException e) {
                logger.error("Error invoking destroy method on bean: {}", beanName, e);
            }
        }


    }
}
