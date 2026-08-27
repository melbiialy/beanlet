package org.study.beanlet.exception;

public class NoUniqueBeanDefinitionException extends RuntimeException {
    public NoUniqueBeanDefinitionException(String message) {
        super(message);
    }
}
