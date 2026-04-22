package org.study.beanlet.context;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;

import org.slf4j.LoggerFactory;
import org.study.beanlet.beans.definition.BeanScope;
import org.study.beanlet.beans.factory.BeanFactory;
import org.study.beanlet.beans.factory.DefaultBeanFactory;
import org.study.beanlet.beans.factory.support.BeanDefinitionRegistry;
import org.study.beanlet.beans.factory.support.beancreator.BeanInstantiator;
import org.study.beanlet.beans.factory.support.beancreator.CreatorRegistry;
import org.study.beanlet.beans.factory.support.beancreator.FactoryCreator;
import org.study.beanlet.beans.factory.support.beanregistry.BeanCacheManager;
import org.study.beanlet.beans.factory.support.beanregistry.BeanScopeRegistry;
import org.study.beanlet.beans.factory.support.beanregistry.SingletonBeanRegistry;
import org.study.beanlet.core.scanning.BeanDefinitionExtractor;
import org.study.beanlet.core.scanning.BeanDefinitionReader;
import org.study.beanlet.core.scanning.BeanDefinitionSource;
import org.study.beanlet.core.scanning.BeanScanner;
import org.study.beanlet.core.scanning.ClassPathBeanScanner;
import org.study.beanlet.core.scanning.ClassPathScanner;
import org.study.beanlet.core.scanning.ComponentExtractor;
import org.study.beanlet.core.scanning.ConfigurationExtractor;
import org.study.beanlet.core.scanning.FileSystemClassPathScanner;
import org.study.beanlet.env.PropertySource;
import org.study.beanlet.env.PropertySourceLoader;
import org.study.beanlet.env.YamlPropertySourceLoader;
import org.study.beanlet.exception.BeanScanningException;
import org.study.beanlet.exception.InitializationException;
import org.study.beanlet.logging.LoggerConfig;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultApplicationContext implements ApplicationContext{
    
    private BeanFactory beanFactory;
    private final Level DEFAULT_LEVEL = Level.INFO;
    private  PropertySource properties;
    private Logger rootLogger;

    public DefaultApplicationContext() {
        properties = getPropertySource();
        rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        configureLoggingLevel();

    }

    private void configureLoggingLevel() {
        if (properties.getProperty("logging.level") != null) {
            String levelStr = properties.getProperty("logging.level");
            Level level = Level.toLevel(levelStr, DEFAULT_LEVEL);
            LoggerConfig.setupLogger(level);
        } else {
            LoggerConfig.setupLogger(DEFAULT_LEVEL);
        }
    }

    @Override
    public Object getBean(String beanName) throws InvocationTargetException, InstantiationException, IllegalAccessException {
       return beanFactory.getBean(beanName);
    }

    @Override
    public Object getQualifiedBean(String beanName, String value) throws InvocationTargetException, InstantiationException, IllegalAccessException {
      return  beanFactory.getQualifiedBean(beanName,value);
    }

    @Override
    public Object getValue(String path) {
        return properties.getProperty(path);
    }

    @Override
    public void refresh() {
        long start = System.currentTimeMillis();
        rootLogger.info("Refreshing application context");
        rootLogger.info("Scanning packages:");
        BeanDefinitionRegistry registry = getBeanDefinitionRegistry(properties);
        rootLogger.info("Found {} beans",registry.getBeanNames().size());
        rootLogger.info("Bean scanning took {} ms",System.currentTimeMillis()-start);
        Map<BeanScope, BeanScopeRegistry> beanScopeRegistryMap = new ConcurrentHashMap<>();
        beanScopeRegistryMap.put(BeanScope.SINGLETON,new SingletonBeanRegistry());
        CreatorRegistry creatorRegistry = new CreatorRegistry(List.of(new BeanInstantiator(),new FactoryCreator()));
        beanFactory = new DefaultBeanFactory(registry,properties,new BeanCacheManager(beanScopeRegistryMap),creatorRegistry);
        preInitializeBeans(registry);
        rootLogger.info("Bean factory initialized successfully in {} ms",System.currentTimeMillis()-start);
    }

    private void preInitializeBeans(BeanDefinitionRegistry registry) {
        for (String beanName : registry.getBeanNames()) {
            try {
                getBean(beanName);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new InitializationException(e.getMessage());
            }
        }
    }

    private static BeanDefinitionRegistry getBeanDefinitionRegistry(PropertySource properties){
        ClassPathScanner classPathScanner = new FileSystemClassPathScanner();
        List<BeanDefinitionExtractor> extractors = List.of(
                new ComponentExtractor(),
                new ConfigurationExtractor()
        );
        BeanDefinitionSource beanDefinitionReader = new BeanDefinitionReader(extractors);
        BeanScanner componentScanner = new ClassPathBeanScanner(properties, classPathScanner, beanDefinitionReader);
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        try {
            componentScanner.scan(registry);
            return registry;
        } catch (Exception e) {
            e.printStackTrace();

            throw new BeanScanningException(e.getMessage());

        }
    }

    private static PropertySource getPropertySource() {
        PropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader();
        return propertySourceLoader.loadProperties("application.yml");
    }
}
