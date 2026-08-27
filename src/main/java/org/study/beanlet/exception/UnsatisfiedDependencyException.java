package org.study.beanlet.exception;

public class UnsatisfiedDependencyException extends RuntimeException {
    public UnsatisfiedDependencyException(String message) {
        super(message);
    }
}
