package org.study.beanlet.registry;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingletonBeanRegistryTest {

    @Test
    void promotesEarlyFactoryResultAndReplacesItWithFinalBean() {
        SingletonBeanRegistry registry = new SingletonBeanRegistry();
        AtomicInteger factoryCalls = new AtomicInteger();
        Object earlyBean = new Object();
        Object finalBean = new Object();
        registry.registerFactory("service", () -> {
            factoryCalls.incrementAndGet();
            return earlyBean;
        });

        assertNull(registry.retrieveBean("service", false));
        assertSame(earlyBean, registry.retrieveBean("service", true));
        assertSame(earlyBean, registry.retrieveBean("service", true));
        assertEquals(1, factoryCalls.get());

        registry.registerBean("service", finalBean);
        assertSame(finalBean, registry.retrieveBean("service", false));
        assertTrue(registry.containsBean("service"));
        assertEquals(1, registry.getBeanCount());

        registry.clear();
        assertFalse(registry.containsBean("service"));
    }
}
