package org.study.beanlet.scanner;

import org.junit.jupiter.api.Test;
import org.study.beanlet.annotation.Bean;
import org.study.beanlet.annotation.Component;
import org.study.beanlet.annotation.Configuration;
import org.study.beanlet.annotation.Lazy;
import org.study.beanlet.annotation.PostConstruct;
import org.study.beanlet.annotation.PreDestroy;
import org.study.beanlet.annotation.Primary;
import org.study.beanlet.annotation.Qualifier;
import org.study.beanlet.annotation.Scope;
import org.study.beanlet.bean.BeanScope;
import org.study.beanlet.env.PropertySource;
import org.study.beanlet.registry.BeanDefinitionRegistry;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeanDefinitionExtractionTest {

    @Test
    void extractsComponentMetadata() {
        var definition = new ComponentExtractor().extract(AnnotatedComponent.class).getFirst();

        assertEquals(AnnotatedComponent.class, definition.getBeanClass());
        assertEquals(BeanScope.PROTOTYPE, definition.getBeanScope());
        assertEquals("annotated", definition.getBeanQualifiedName());
        assertTrue(definition.isLazy());
        assertTrue(definition.isPrimary());
        assertEquals("init", definition.getInitMethod().getName());
        assertEquals("destroy", definition.getDestroyMethod().getName());
    }

    @Test
    void extractsBeanMethodsFromConfiguration() {
        var definitions = new ConfigurationExtractor().extract(TestConfiguration.class);

        assertEquals(1, definitions.size());
        assertEquals(ProducedBean.class, definitions.getFirst().getBeanClass());
        assertEquals("produced", definitions.getFirst().getBeanQualifiedName());
        assertEquals("producedBean", definitions.getFirst().getFactoryMethod().getName());
        assertEquals("TestConfiguration", definitions.getFirst().getFactoryBeanName());
    }

    @Test
    void scannerUsesConfiguredBasePackageAndPassesFoundClassesToReader() throws Exception {
        AtomicReference<String> scannedPackage = new AtomicReference<>();
        AtomicReference<Set<Class<?>>> readClasses = new AtomicReference<>();
        ClassPathScanner classPathScanner = (basePackage, classes) -> {
            scannedPackage.set(basePackage);
            classes.add(AnnotatedComponent.class);
        };
        BeanDefinitionSource reader = (classes, registry) -> readClasses.set(classes);
        PropertySource properties = new PropertySource("test", new HashMap<>());
        properties.addProperty("beanlet.scan.base-package", "example.components");

        new ClassPathBeanScanner(properties, classPathScanner, reader).scan(new BeanDefinitionRegistry());

        assertEquals("example.components", scannedPackage.get());
        assertTrue(readClasses.get().contains(AnnotatedComponent.class));
    }

    @Test
    void definitionReaderRegistersComponentsAndBeansProducedByConfigurations() {
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        BeanDefinitionReader reader = new BeanDefinitionReader(
                java.util.List.of(new ComponentExtractor(), new ConfigurationExtractor()));

        reader.readBeanDefinition(Set.of(AnnotatedComponent.class, TestConfiguration.class), registry);

        assertTrue(registry.containsBeanDefinition("AnnotatedComponent"));
        assertTrue(registry.containsBeanDefinition("TestConfiguration"));
        assertTrue(registry.containsBeanDefinition("ProducedBean"));
        assertFalse(registry.getBeanDefinition("AnnotatedComponent").getBeanScope() == BeanScope.SINGLETON);
    }

    @Component
    @Scope(BeanScope.PROTOTYPE)
    @Qualifier("annotated")
    @Lazy
    @Primary
    static class AnnotatedComponent {
        @PostConstruct void init() { }
        @PreDestroy void destroy() { }
    }

    @Configuration
    static class TestConfiguration {
        @Bean ProducedBean producedBean() { return new ProducedBean(); }
    }

    @Qualifier("produced")
    static class ProducedBean { }
}
