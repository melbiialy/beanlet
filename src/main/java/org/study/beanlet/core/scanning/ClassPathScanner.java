package org.study.beanlet.core.scanning;

import java.util.Set;

public interface ClassPathScanner {
    void loadClasses(String packageName, Set<Class<?>> classes) throws ClassNotFoundException;
}

