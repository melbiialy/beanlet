package org.study.beanlet.env;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueResolverTest {

    @Test
    void convertsSupportedPrimitiveAndWrapperTypes() {
        assertEquals(42, ValueResolver.resolveValue("42", int.class));
        assertEquals(42L, ValueResolver.resolveValue("42", Long.class));
        assertEquals(2.5d, ValueResolver.resolveValue("2.5", double.class));
        assertEquals(true, ValueResolver.resolveValue("true", Boolean.class));
        assertEquals('b', ValueResolver.resolveValue("beanlet", char.class));
        assertEquals((short) 7, ValueResolver.resolveValue("7", short.class));
    }

    @Test
    void leavesStringsUntouched() {
        assertEquals("beanlet", ValueResolver.resolveValue("beanlet", String.class));
    }
}
