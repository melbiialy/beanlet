package org.study.beanlet.core.scanning;

import org.study.beanlet.core.util.ReflectionUtils;

import java.io.File;
import java.net.URL;
import java.util.Set;

public class FileSystemClassPathScanner implements ClassPathScanner {

    @Override
    public void loadClasses(String packageName, Set<Class<?>> classes) throws ClassNotFoundException {
        String path = packageName.replace('.', '/');

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return;
        }

        File directory = new File(resource.getFile());
        if (!directory.exists()) {
            return;
        }

        scanDirectory(directory, packageName, classes);
    }

    private void scanDirectory(File directory, String packageName, Set<Class<?>> classes)
            throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." +
                        file.getName().replace(".class", "");
                if (className.startsWith(".")){
                    className = className.substring(1);
                }
                classes.add(ReflectionUtils.loadClass(className));
            }
        }
    }
}

