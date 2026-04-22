package org.study.beanlet.value;

public interface ValueResolver {
    Object resolveValue(String value, Class<?> targetType);
}
