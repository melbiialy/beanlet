package org.study.ioc.componentscan;

import org.study.ioc.annotation.Component;
import org.study.ioc.beans.defintion.BeanDefinition;
import org.study.ioc.beans.factory.support.BeanDefinitionRegistry;
import org.study.ioc.utils.ReflectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ComponentScanner {
    private List<String > packages;
    private final String DEFAULT_PACKAGE = "";
    private String basePackage;


    public ComponentScanner() {
        packages = new ArrayList<>();
        basePackage = DEFAULT_PACKAGE;
    }
    public void addPackage(String packageName){
        packages.add(packageName);
    }
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
    public List<String> getPackages() {
        return packages;
    }
    public void setPackages(List<String> packages) {
        this.packages = packages;
    }
    public void scan(BeanDefinitionRegistry registry) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("")).getPath());
        scanDirectory(file, basePackage,registry);
    }

    private void scanDirectory(File file, String basePackage, BeanDefinitionRegistry registry) throws ClassNotFoundException {
        File[] files = file.listFiles();

        if (files == null) {
            return;
        }
        Arrays.sort(files);
        for (File f : files) {
            if (f.isDirectory()) {
                String currentPackage = basePackage.isEmpty()?f.getName():basePackage+"."+f.getName();
                scanDirectory(f,currentPackage, registry);
                continue;
            }
            if (!f.getName().endsWith(".class")) {
                continue;
            }
            String beanName = f.getName().replace(".class", "");
            String path = basePackage+"."+beanName;

            Class<?> clazz = Class.forName(path);
            if (clazz.isAnnotationPresent(Component.class)) {
                BeanDefinition beanDefinition = ReflectionUtils.extractBeanDefinition(clazz);
                registry.registerBeanDefinition(clazz.getCanonicalName(), beanDefinition);
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    if (!anInterface.getCanonicalName().endsWith("Object")) {
                        registry.addTypeInjectionCache(anInterface.getName(), clazz.getCanonicalName());
                    }
                }

            }
        }
    }
}
