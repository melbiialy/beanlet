package org.study.beanlet.context;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;

import org.slf4j.LoggerFactory;
import org.study.beanlet.exception.BeanScanningException;
import org.study.beanlet.exception.InitializationException;
import org.study.beanlet.beans.factory.BeanFactory;
import org.study.beanlet.beans.factory.DefaultBeanFactory;
import org.study.beanlet.beans.factory.support.BeanDefinitionRegistry;
import org.study.beanlet.core.scanning.ComponentScanner;
import org.study.beanlet.env.PropertySource;
import org.study.beanlet.env.PropertySourceLoader;
import org.study.beanlet.env.YamlPropertySourceLoader;
import org.study.beanlet.logging.LoggerConfig;


import java.lang.reflect.InvocationTargetException;
import java.sql.Time;
import java.util.Timer;


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
    public void refresh() {
        long start = System.currentTimeMillis();
        rootLogger.info("Refreshing application context");
        rootLogger.info("Scanning packages:");
        BeanDefinitionRegistry registry = getBeanDefinitionRegistry(properties);
        rootLogger.info("Found {} beans",registry.getBeanNames().size());
        rootLogger.info("Bean scanning took {} ms",System.currentTimeMillis()-start);
        beanFactory = new DefaultBeanFactory(registry,properties);
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
        ComponentScanner componentScanner = new ComponentScanner();
        String basePackage = properties.getProperty("spring.main.base-package");
        if (basePackage != null) {
            componentScanner.setBasePackage(basePackage);
        }
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        try {
            componentScanner.scan(registry);
            return registry;
        } catch (ClassNotFoundException e) {
            throw new BeanScanningException(e.getMessage());

        }
    }

    private static PropertySource getPropertySource() {
        PropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader();
        return propertySourceLoader.loadProperties("application.yml");
    }
}
