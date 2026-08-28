package org.study.beanlet.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.study.beanlet.bean.BeanWrapper;
import org.study.beanlet.exception.BeanNotFoundException;
import org.study.beanlet.processor.BeanPostProcessor;
import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.bean.BeanScope;
import org.study.beanlet.processor.InstantiationAwareBeanPostProcessor;
import org.study.beanlet.processor.SmartInstantiationAwareBeanPostProcessor;
import org.study.beanlet.registry.BeanDefinitionRegistry;
import org.study.beanlet.support.CreationTracker;
import org.study.beanlet.registry.BeanCacheManager;
import org.study.beanlet.logging.CircularDependencyReporter;
import org.study.beanlet.env.PropertySource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public  class DefaultBeanFactory implements BeanFactory, AutoCloseable {
    private final BeanDefinitionRegistry registry;
    private final CreationTracker creationTracker;
    private final Logger logger =  LoggerFactory.getLogger(DefaultBeanFactory.class);
    private final BeanCacheManager beanCacheManager;
    private final PropertySource properties;
    List<BeanPostProcessor> beanPostProcessors;

    public DefaultBeanFactory(BeanDefinitionRegistry registry, PropertySource properties, BeanCacheManager beanCacheManager, List<BeanPostProcessor> beanPostProcessors) {
        this.registry = registry;
        this.creationTracker = new CreationTracker();
        this.beanCacheManager = beanCacheManager;
        this.properties = properties;
        this.beanPostProcessors = beanPostProcessors;
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        logger.trace("Getting bean: {}", beanName);
        BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
        if (beanDefinition == null) {
            throw new BeanNotFoundException("No bean found for name: " + beanName);
        }

        Object bean = doGetBean(beanName,beanDefinition.getBeanScope());

        if (bean != null) {
            return bean;
        }
        bean = createBean(beanName, beanDefinition);
        populateBean(beanName, bean, beanDefinition);
        initializeBean(bean,beanName,beanDefinition);
        return bean;
    }

    private void initializeBean(Object bean, String beanName, BeanDefinition beanDefinition) throws InvocationTargetException, IllegalAccessException {
        Method method = beanDefinition.getInitMethod();
        if (method == null) {
            return;
        }
        method.setAccessible(true);
        try {
            method.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw e;
        }
    }

    private void populateBean(String beanName, Object bean, BeanDefinition beanDefinition)  {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            if (beanPostProcessor instanceof SmartInstantiationAwareBeanPostProcessor siabp ) {
                if (siabp.postProcessAfterInitialization(bean,beanName)){
                    creationTracker.finalizeCreationPhase(beanName);
                    beanCacheManager.registerBean(beanName,beanDefinition.getBeanScope(),bean);
                    return;
                }
            }
        }
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor istp ) {
                istp.postProcessAfterInitialization(bean,beanName);
            }
        }
        creationTracker.finalizeCreationPhase(beanName);
        beanCacheManager.registerBean(beanName,beanDefinition.getBeanScope(),bean);
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) throws Exception {
        logger.trace("Creating bean: {}", beanName);
        creationTracker.markAsUnderInstantiation(beanName);

        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor bpp) {
                Object shortCircuit = bpp.postProcessBeforeInitialization(beanDefinition.getBeanClass(), beanName);
                if (shortCircuit != null) {
                    return shortCircuit;
                }
            }
        }
        Constructor<?> constructor = null;
        for (BeanPostProcessor bp : beanPostProcessors) {
            if (bp instanceof SmartInstantiationAwareBeanPostProcessor smart) {
                Constructor<?> candidate = smart.determineCandidateConstructor(beanDefinition.getBeanClass(), beanName);
                if (candidate != null) {
                    constructor = candidate;
                    break;
                }
            }
        }
        if (constructor == null) {
            constructor = beanDefinition.getBeanClass().getDeclaredConstructor();
        }
        BeanWrapper beanWrapper = new BeanWrapper(beanDefinition,this,constructor);
        Object bean = beanWrapper.getBean();
        beanCacheManager.registerEarlyFactoryBean(beanName,()->resolveEarlyRef(bean,beanName),beanDefinition.getBeanScope());

        creationTracker.finishInstantiation(beanName);
        return bean;
    }

    private Object resolveEarlyRef(Object bean, String beanName) {
        Object exposedObject = bean;

        for (BeanPostProcessor bp : beanPostProcessors) {
            if (bp instanceof InstantiationAwareBeanPostProcessor iabp) {
                exposedObject = iabp.getEarlyBeanReference(exposedObject, beanName);
            }
        }

        return exposedObject;
    }

    private Object doGetBean(String beanName, BeanScope beanScope) {
        Object bean = beanCacheManager.getBean(beanName,creationTracker.allowEarlyRef(beanName),beanScope);
        if (bean != null) {
            logger.trace("Bean {} found in scope {}", beanName, beanScope);
            return bean;
        }
        if (creationTracker.isUnderCreationPhase(beanName)) {
            logger.trace("Bean {} is still being created", beanName);
            logger.trace("Getting early reference for bean {}: {}", beanName, null);
            logger.error("Circular dependency detected for bean: {}", beanName);
            CircularDependencyReporter.reportError(creationTracker.getBeanNames(), beanName);
        }
        return null;
    }


    @Override
    public String getValue(String path) {
        return properties.getProperty(path);
    }


    @Override
    public Object getBeanByType(Class<?> dependencyType, String qualifierValue) throws Exception {
       String beanName = registry.getTypeMatchBeanDefinition(dependencyType, qualifierValue);
       return getBean(beanName);
    }

    @Override
    public void registerBean(String beanName, Object bean) throws Exception {
        this.beanCacheManager.registerBean(beanName,BeanScope.SINGLETON,bean);
    }

    @Override
    public void close()  {
        logger.info("Shutting down BeanFactory, destroying singleton beans...");
        for (String beanName : registry.getBeanNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (beanDefinition.getBeanScope() != BeanScope.SINGLETON){
                continue;
            }
            Object bean = beanCacheManager.getBean(beanName,false,beanDefinition.getBeanScope());
            if (bean == null) continue;

            if (beanDefinition.getDestroyMethod() != null) {
                try {
                    beanDefinition.getDestroyMethod().invoke(bean);
                    logger.trace("Destroy method invoked on bean: {}", beanName);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    logger.error("Error invoking destroy method on bean: {}", beanName, e);
                }
            }

            if (bean instanceof AutoCloseable closeable) {
                try {
                    closeable.close();
                    logger.trace("Closed AutoCloseable bean: {}", beanName);
                } catch (Exception e) {
                    logger.error("Error closing bean: {}", beanName, e);
                }
            }
        }
    }
}
