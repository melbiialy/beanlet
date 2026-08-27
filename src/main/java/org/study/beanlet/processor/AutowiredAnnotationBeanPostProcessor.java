package org.study.beanlet.processor;

import org.study.beanlet.annotation.Autowired;
import org.study.beanlet.annotation.Qualifier;
import org.study.beanlet.exception.CannotResolveConstructorException;
import org.study.beanlet.exception.UnsatisfiedDependencyException;
import org.study.beanlet.factory.BeanFactory;

import java.lang.reflect.*;

public class AutowiredAnnotationBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor, InstantiationAwareBeanPostProcessor, BeanPostProcessor {

    @Override
    public void postProcessProperties(Object bean, String beanName, BeanFactory beanFactory) throws Exception {
        Class<?> beanClass = bean.getClass();

        injectFields(bean, beanName, beanFactory, beanClass);
        injectMethods(bean, beanName, beanFactory, beanClass);
    }


    private void injectFields(Object bean, String beanName, BeanFactory beanFactory, Class<?> beanClass) throws Exception {
        Class<?> current = beanClass;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (autowired == null) {
                    continue;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Object value = resolveFieldValue(field, beanName, beanFactory, autowired.required());
                if (value == null) {
                    continue;
                }
                field.setAccessible(true);
                field.set(bean, value);
            }
            current = current.getSuperclass();
        }
    }


    private void injectMethods(Object bean, String beanName, BeanFactory beanFactory, Class<?> targetClass) throws Exception {
        Class<?> current = targetClass;
        while (current != null && current != Object.class) {
            for (Method method : current.getDeclaredMethods()) {
                Autowired autowired = method.getAnnotation(Autowired.class);
                if (autowired == null) {
                    continue;
                }
                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                Object[] args = resolveMethodArgs(method, beanName, beanFactory, autowired.required());
                if (args == null) {
                    continue;
                }
                method.setAccessible(true);
                method.invoke(bean, args);
            }
            current = current.getSuperclass();
        }
    }

    private String extractQualifierValue(AnnotatedElement element) {
        Qualifier qualifier = element.getAnnotation(Qualifier.class);
        return (qualifier != null) ? qualifier.value() : null;
    }

    private Object resolveFieldValue(Field field, String beanName, BeanFactory beanFactory, boolean required) {
        Class<?> dependencyType = field.getType();
        String qualifierValue = extractQualifierValue(field);
        try {
            return beanFactory.getBeanByType(dependencyType, qualifierValue);
        } catch (Exception ex) {
            if (required) {
                throw new UnsatisfiedDependencyException(
                        "Cannot resolve dependency " + dependencyType.getName() + " for bean " + beanName);
            }
            return null;
        }
    }

    private Object[] resolveMethodArgs(Method method, String beanName, BeanFactory beanFactory, boolean required)  {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();
            String qualifierValue = extractQualifierValue(parameters[i]);
            try {
                args[i] = beanFactory.getBeanByType(paramType, qualifierValue);
            } catch (Exception ex) {
                if (required) {
                    throw new UnsatisfiedDependencyException(
                            "Cannot resolve dependency " + paramType.getName() + " for bean " + beanName);
                }
                return null;
            }
        }
        return args;
    }


    @Override
    public Constructor<?> determineCandidateConstructor(Class<?> beanClass, String beanName) {
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        Constructor<?> autowiredConstructor = null;

        for (Constructor<?> constructor : constructors) {
            if (constructor.getAnnotation(Autowired.class) != null) {
                if (autowiredConstructor != null) {
                    throw new CannotResolveConstructorException(
                            "cannot use more than one @Autowired constructor for bean " + beanName);
                }
                autowiredConstructor = constructor;
            }
        }

        if (autowiredConstructor != null) {
            return autowiredConstructor;
        }

        try {
            return beanClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            if (constructors.length == 1) {
                return constructors[0];
            }
            throw new CannotResolveConstructorException("cannot find constructor for bean " + beanName);
        }
    }


}