package org.study.beanlet.context;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;

import org.slf4j.LoggerFactory;
import org.study.beanlet.bean.BeanScope;
import org.study.beanlet.factory.BeanFactory;
import org.study.beanlet.factory.DefaultBeanFactory;
import org.study.beanlet.registry.BeanDefinitionRegistry;
import org.study.beanlet.instantiation.ConstructorInstantiator;
import org.study.beanlet.instantiation.BeanCreationStrategyResolver;
import org.study.beanlet.instantiation.FactoryMethodInstantiator;
import org.study.beanlet.registry.BeanCacheManager;
import org.study.beanlet.registry.BeanScopeRegistry;
import org.study.beanlet.registry.SingletonBeanRegistry;
import org.study.beanlet.scanner.BeanDefinitionExtractor;
import org.study.beanlet.scanner.BeanDefinitionReader;
import org.study.beanlet.scanner.BeanDefinitionSource;
import org.study.beanlet.scanner.BeanScanner;
import org.study.beanlet.scanner.ClassPathBeanScanner;
import org.study.beanlet.scanner.ClassPathScanner;
import org.study.beanlet.scanner.ComponentExtractor;
import org.study.beanlet.scanner.ConfigurationExtractor;
import org.study.beanlet.scanner.FileSystemClassPathScanner;
import org.study.beanlet.env.PropertySource;
import org.study.beanlet.env.PropertySourceLoader;
import org.study.beanlet.env.YamlPropertySourceLoader;
import org.study.beanlet.exception.BeanScanningException;
import org.study.beanlet.exception.InitializationException;
import org.study.beanlet.logging.LoggerConfig;


import java.io.IOException;
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
        String levelStr = properties.getProperty("${logging.level}");
        if (levelStr!= null) {
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
    public String  getValue(String path) {
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
        BeanCreationStrategyResolver creatorRegistry = new BeanCreationStrategyResolver(List.of(new ConstructorInstantiator(),new FactoryMethodInstantiator()));
        beanFactory = new DefaultBeanFactory(registry,properties,new BeanCacheManager(beanScopeRegistryMap),creatorRegistry);
        preInitializeBeans(registry);
        rootLogger.info("Bean factory initialized successfully in {} ms",System.currentTimeMillis()-start);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                beanFactory.close();
            } catch (Exception e) {
                rootLogger.error("Error during shutdown", e);
            }
        }));
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

    @Override
    public void close() throws Exception {
        beanFactory.close();
    }
}
