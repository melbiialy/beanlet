package org.study.beanlet.context;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;

import org.slf4j.LoggerFactory;
import org.study.beanlet.bean.BeanScope;
import org.study.beanlet.factory.BeanFactory;
import org.study.beanlet.factory.DefaultBeanFactory;
import org.study.beanlet.processor.BeanFactoryPostProcessor;
import org.study.beanlet.registry.BeanDefinitionRegistry;
import org.study.beanlet.registry.BeanCacheManager;
import org.study.beanlet.registry.BeanScopeRegistry;
import org.study.beanlet.registry.SingletonBeanRegistry;
import org.study.beanlet.scanner.*;
import org.study.beanlet.env.PropertySource;
import org.study.beanlet.env.PropertySourceLoader;
import org.study.beanlet.env.YamlPropertySourceLoader;
import org.study.beanlet.exception.BeanScanningException;
import org.study.beanlet.exception.InitializationException;
import org.study.beanlet.logging.LoggerConfig;


import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultApplicationContext implements ApplicationContext, Closeable {
    
    private BeanFactory beanFactory;
    private final Level DEFAULT_LEVEL = Level.INFO;
    private final PropertySource properties;
    private final Logger rootLogger;
    private final ProcessorScanner processorScanner;
    private final SingletonBeanRegistry singletonBeanRegistry;
    private List<BeanFactoryPostProcessor>  beanFactoryPostProcessors;

    public DefaultApplicationContext() throws ClassNotFoundException {
        this.processorScanner = new ProcessorScanner(new FileSystemClassPathScanner());
        properties = getPropertySource();
        rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        configureLoggingLevel();
        singletonBeanRegistry = new SingletonBeanRegistry();

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
    public Object getBean(String beanName) throws Exception {
       return singletonBeanRegistry.retrieveBean(beanName,true);
    }



    public String  getValue(String path) {
        return properties.getProperty(path);
    }


    public Object getBeanByType(Class<?> dependencyType, String value) throws Exception {
        return null;
    }

    @Override
    public void refresh() throws Exception {
        long start = System.currentTimeMillis();
        rootLogger.info("Refreshing application context");
        rootLogger.info("Scanning packages:");

        BeanDefinitionRegistry registry = getBeanDefinitionRegistry(properties);
        rootLogger.info("Found {} beans",registry.getBeanNames().size());
        rootLogger.info("Bean scanning took {} ms",System.currentTimeMillis()-start);
        Map<BeanScope, BeanScopeRegistry> beanScopeRegistryMap = new ConcurrentHashMap<>();
        beanScopeRegistryMap.put(BeanScope.SINGLETON,singletonBeanRegistry);
        beanFactory = new DefaultBeanFactory(registry,properties,new BeanCacheManager(beanScopeRegistryMap),processorScanner.getBeanPostProcessors());
        handleBeanFactoryProcessors(registry);
        registerCoreDefinition(registry);
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

    private void registerCoreDefinition(BeanDefinitionRegistry registry) {
    }

    private void handleBeanFactoryProcessors(BeanDefinitionRegistry registry) {
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : processorScanner.getBeanFactoryPostProcessors()) {
            if (beanFactoryPostProcessor != null) {
                beanFactoryPostProcessor.postProcessorBeanFactory(registry);
            }
        }
    }


    private void preInitializeBeans(BeanDefinitionRegistry registry) {
        for (String beanName : registry.getBeanNames()) {
            try {
                getBean(beanName);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new InitializationException(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
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


    public void close() {
        try {
            beanFactory.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
