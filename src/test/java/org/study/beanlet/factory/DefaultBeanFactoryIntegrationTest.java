package org.study.beanlet.factory;

import org.junit.jupiter.api.Test;
import org.study.beanlet.annotation.Autowired;
import org.study.beanlet.annotation.PostConstruct;
import org.study.beanlet.annotation.PreDestroy;
import org.study.beanlet.annotation.Value;
import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.bean.BeanDefinitionBuilder;
import org.study.beanlet.bean.BeanScope;
import org.study.beanlet.env.PropertySource;
import org.study.beanlet.processor.AutowiredAnnotationBeanPostProcessor;
import org.study.beanlet.registry.BeanCacheManager;
import org.study.beanlet.registry.BeanDefinitionRegistry;
import org.study.beanlet.registry.BeanScopeRegistry;
import org.study.beanlet.registry.SingletonBeanRegistry;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultBeanFactoryIntegrationTest {

    @Test
    void createsAndWiresBeansAndRunsLifecycleCallbacks() throws Exception {
        DefaultBeanFactory factory = factoryFor(
                definition(Repository.class, BeanScope.SINGLETON),
                definition(Service.class, BeanScope.SINGLETON));

        Service service = (Service) factory.getBean("Service");
        Service sameService = (Service) factory.getBean("Service");

        assertSame(service, sameService);
        assertTrue(service.initialized);
        assertSame(service.repository, factory.getBean("Repository"));

        factory.close();
        assertTrue(service.destroyed);
    }

    @Test
    void createsASeparateInstanceForEachPrototypeLookup() throws Exception {
        DefaultBeanFactory factory = factoryFor(definition(PrototypeBean.class, BeanScope.PROTOTYPE));

        assertNotSame(factory.getBean("PrototypeBean"), factory.getBean("PrototypeBean"));
    }

    @Test
    void resolvesCircularFieldDependenciesUsingEarlySingletonReferences() throws Exception {
        DefaultBeanFactory factory = factoryFor(
                definition(First.class, BeanScope.SINGLETON),
                definition(Second.class, BeanScope.SINGLETON));

        First first = (First) factory.getBean("First");
        Second second = (Second) factory.getBean("Second");

        assertSame(second, first.second);
        assertSame(first, second.first);
    }

    @Test
    void resolvesConstructorAndAutowiredMethodDependencies() throws Exception {
        DefaultBeanFactory factory = factoryFor(
                definition(Repository.class, BeanScope.SINGLETON),
                definition(ConstructorConsumer.class, BeanScope.SINGLETON),
                definition(MethodConsumer.class, BeanScope.SINGLETON));

        ConstructorConsumer constructorConsumer = (ConstructorConsumer) factory.getBean("ConstructorConsumer");
        MethodConsumer methodConsumer = (MethodConsumer) factory.getBean("MethodConsumer");

        assertSame(factory.getBean("Repository"), constructorConsumer.repository);
        assertSame(factory.getBean("Repository"), methodConsumer.repository);
    }

    @Test
    void createsBeansThroughFactoryMethodsAndResolvesValueParameters() throws Exception {
        BeanDefinition configuration = definition(FactoryConfiguration.class, BeanScope.SINGLETON);
        BeanDefinition product = new BeanDefinitionBuilder().beanClass(ConfigProduct.class)
                .scope(BeanScope.SINGLETON).beanQualifiedName("ConfigProduct")
                .factoryMethod(FactoryConfiguration.class.getDeclaredMethod("configProduct", String.class))
                .factoryBeanName("FactoryConfiguration").build();
        DefaultBeanFactory factory = factoryFor(Map.of("product.name", "from-properties"), configuration, product);

        ConfigProduct result = (ConfigProduct) factory.getBean("ConfigProduct");

        assertEquals("from-properties", result.name);
    }

    private static DefaultBeanFactory factoryFor(BeanDefinition... definitions) {
        return factoryFor(Map.of(), definitions);
    }

    private static DefaultBeanFactory factoryFor(Map<String, String> properties, BeanDefinition... definitions) {
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        for (BeanDefinition definition : definitions) {
            registry.registerBeanDefinition(definition.getBeanClass().getSimpleName(), definition);
        }
        BeanScopeRegistry singletonRegistry = new SingletonBeanRegistry();
        return new DefaultBeanFactory(registry, new PropertySource("test", properties),
                new BeanCacheManager(Map.of(BeanScope.SINGLETON, singletonRegistry)),
                List.of(new AutowiredAnnotationBeanPostProcessor()));
    }

    private static BeanDefinition definition(Class<?> type, BeanScope scope) {
        try {
            return new BeanDefinitionBuilder().beanClass(type).scope(scope)
                    .beanQualifiedName(type.getSimpleName())
                    .initMethod(type.getDeclaredMethod("init"))
                    .destroyMethod(type.getDeclaredMethod("destroy"))
                    .build();
        } catch (NoSuchMethodException ignored) {
            return new BeanDefinitionBuilder().beanClass(type).scope(scope)
                    .beanQualifiedName(type.getSimpleName()).build();
        }
    }

    static class Repository { }

    static class Service {
        @Autowired Repository repository;
        boolean initialized;
        boolean destroyed;

        @PostConstruct void init() { initialized = true; }
        @PreDestroy void destroy() { destroyed = true; }
    }

    static class PrototypeBean { }

    static class First { @Autowired Second second; }
    static class Second { @Autowired First first; }

    static class ConstructorConsumer {
        final Repository repository;
        ConstructorConsumer(Repository repository) { this.repository = repository; }
    }

    static class MethodConsumer {
        Repository repository;
        @Autowired void setRepository(Repository repository) { this.repository = repository; }
    }

    static class FactoryConfiguration {
        ConfigProduct configProduct(@Value("${product.name}") String name) {
            return new ConfigProduct(name);
        }
    }

    static class ConfigProduct {
        final String name;
        ConfigProduct(String name) { this.name = name; }
    }
}
