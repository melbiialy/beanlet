package org.study.ioc.beans.factory.support;

import org.study.ioc.annotation.Autowired;

import java.lang.reflect.Constructor;

public class ConstructorResolver {
    public Constructor<?> resolveConstructor(Constructor<?>[] constructors) {
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
