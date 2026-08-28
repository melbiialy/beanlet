package org.study.beanlet.context;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.study.beanlet.bean.BeanScope;
import org.study.beanlet.env.PropertySource;
import org.study.beanlet.env.PropertySourceLoader;
import org.study.beanlet.env.YamlPropertySourceLoader;
import org.study.beanlet.exception.BeanScanningException;
import org.study.beanlet.exception.InitializationException;
import org.study.beanlet.factory.BeanFactory;
import org.study.beanlet.factory.DefaultBeanFactory;
import org.study.beanlet.logging.LoggerConfig;
import org.study.beanlet.processor.BeanFactoryPostProcessor;
import org.study.beanlet.registry.BeanCacheManager;
import org.study.beanlet.registry.BeanDefinitionRegistry;
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
import org.study.beanlet.scanner.ProcessorScanner;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultApplicationContext implements ApplicationContext, Closeable {

    private static final Level DEFAULT_LOG_LEVEL = Level.INFO;
    private static final String LOG_LEVEL_PROPERTY = "${logging.level}";

    private final Logger rootLogger;
    private final PropertySource properties;
    private final ProcessorScanner processorScanner;
    private final SingletonBeanRegistry singletonBeanRegistry;

    private BeanFactory beanFactory;

    public DefaultApplicationContext() throws ClassNotFoundException {
        this.processorScanner = new ProcessorScanner(new FileSystemClassPathScanner());
        this.properties = loadPropertySource();
        this.rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        this.singletonBeanRegistry = new SingletonBeanRegistry();
        configureLoggingLevel();
    }

    // -------------------------------------------------------------------------
    // ApplicationContext
    // -------------------------------------------------------------------------

    @Override
    public void refresh() throws Exception {
        long start = System.currentTimeMillis();
        rootLogger.info("Refreshing application context");

        BeanDefinitionRegistry registry = scanBeanDefinitions();
        rootLogger.info("Found {} bean definitions in {} ms",
                registry.getBeanNames().size(), elapsed(start));

        beanFactory = createBeanFactory(registry);
        applyBeanFactoryPostProcessors(registry);
        preInitializeBeans(registry);

        rootLogger.info("Application context refreshed in {} ms", elapsed(start));
        registerShutdownHook();
    }

    @Override
    public Object getBean(String beanName) throws Exception {
            return beanFactory.getBean(beanName);

    }

    // -------------------------------------------------------------------------
    // Additional public API
    // -------------------------------------------------------------------------

    public String getValue(String path) {
        return properties.getProperty(path);
    }

    public Object getBeanByType(Class<?> dependencyType, String qualifier) throws Exception {
        if (beanFactory == null) {
            return null;
        }
        return beanFactory.getBeanByType(dependencyType, qualifier);
    }

    // -------------------------------------------------------------------------
    // Closeable
    // -------------------------------------------------------------------------

    @Override
    public void close() {
        if (beanFactory != null) {
            try {
                beanFactory.close();
            } catch (Exception e) {
                throw new RuntimeException("Failed to close BeanFactory", e);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void configureLoggingLevel() {
        String levelStr = properties.getProperty(LOG_LEVEL_PROPERTY);
        Level level = (levelStr != null) ? Level.toLevel(levelStr, DEFAULT_LOG_LEVEL) : DEFAULT_LOG_LEVEL;
        LoggerConfig.setupLogger(level);
    }

    private BeanDefinitionRegistry scanBeanDefinitions() {
        ClassPathScanner classPathScanner = new FileSystemClassPathScanner();
        List<BeanDefinitionExtractor> extractors = List.of(
                new ComponentExtractor(),
                new ConfigurationExtractor()
        );
        BeanDefinitionSource reader = new BeanDefinitionReader(extractors);
        BeanScanner scanner = new ClassPathBeanScanner(properties, classPathScanner, reader);
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        try {
            scanner.scan(registry);
        } catch (Exception e) {
            throw new BeanScanningException(e.getMessage());
        }
        return registry;
    }

    private BeanFactory createBeanFactory(BeanDefinitionRegistry registry) {
        Map<BeanScope, BeanScopeRegistry> scopeRegistries = new ConcurrentHashMap<>();
        scopeRegistries.put(BeanScope.SINGLETON, singletonBeanRegistry);
        return new DefaultBeanFactory(
                registry,
                properties,
                new BeanCacheManager(scopeRegistries),
                processorScanner.getBeanPostProcessors()
        );
    }

    private void applyBeanFactoryPostProcessors(BeanDefinitionRegistry registry) {
        for (BeanFactoryPostProcessor processor : processorScanner.getBeanFactoryPostProcessors()) {
            if (processor != null) {
                processor.postProcessorBeanFactory(registry);
            }
        }
    }

    private void preInitializeBeans(BeanDefinitionRegistry registry) {
        for (String beanName : registry.getBeanNames()) {
            try {
                beanFactory.getBean(beanName);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new InitializationException(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize bean: " + beanName, e);
            }
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                beanFactory.close();
            } catch (Exception e) {
                rootLogger.error("Error during context shutdown", e);
            }
        }, "context-shutdown-hook"));
    }

    private static PropertySource loadPropertySource() {
        PropertySourceLoader loader = new YamlPropertySourceLoader();
        return loader.loadProperties("application.yml");
    }

    private static long elapsed(long startMs) {
        return System.currentTimeMillis() - startMs;
    }
}
