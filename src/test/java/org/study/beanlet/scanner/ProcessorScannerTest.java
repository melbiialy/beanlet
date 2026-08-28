package org.study.beanlet.scanner;

import org.junit.jupiter.api.Test;
import org.study.beanlet.processor.BeanFactoryPostProcessor;
import org.study.beanlet.processor.BeanPostProcessor;
import org.study.beanlet.registry.BeanDefinitionRegistry;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessorScannerTest {

    @Test
    void discoversConcretePostProcessorsAndSeparatesFactoryProcessors() throws Exception {
        ClassPathScanner scanner = (basePackage, classes) -> classes.addAll(Set.of(
                PlainPostProcessor.class, RegistryPostProcessor.class, BeanPostProcessor.class));

        ProcessorScanner processorScanner = new ProcessorScanner(scanner);

        assertEquals(1, processorScanner.getBeanPostProcessors().size());
        assertTrue(processorScanner.getBeanPostProcessors().getFirst() instanceof PlainPostProcessor);
        assertEquals(1, processorScanner.getBeanFactoryPostProcessors().size());
        assertTrue(processorScanner.getBeanFactoryPostProcessors().getFirst() instanceof RegistryPostProcessor);
    }

    static class PlainPostProcessor implements BeanPostProcessor { }

    static class RegistryPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessorBeanFactory(BeanDefinitionRegistry registry) { }
    }
}
