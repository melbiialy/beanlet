package org.study.beanlet.support;

import org.study.beanlet.annotation.Autowired;

import java.lang.reflect.Constructor;

public class ConstructorResolver {
    public static Constructor<?> resolveConstructor(Constructor<?>[] constructors) {
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)){
                return constructor;
            }
        }
        if (constructors.length == 1){
            return constructors[0];
        }else {
            throw new RuntimeException("No suitable constructor found");
        }

    }
}
