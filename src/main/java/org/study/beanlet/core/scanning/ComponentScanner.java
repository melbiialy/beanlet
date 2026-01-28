package org.study.beanlet.core.scanning;

import org.study.beanlet.annotation.Component;
import org.study.beanlet.annotation.Configuration;
import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.factory.support.BeanDefinitionRegistry;
import org.study.beanlet.core.util.ReflectionUtils;
import org.study.beanlet.env.PropertySource;

import java.io.File;


public class ComponentScanner implements Scanner{
    private String basePackage;
    private final Extractor componentExtractor;
    private final Extractor configurationExtractor;
    private final String DEFAULT_BASE_PACKAGE = "";

    public ComponentScanner(PropertySource propertySource) {
        initPackages(propertySource);
        componentExtractor = new ComponentExtractor();
        configurationExtractor = new ConfigurationExtractor();
    }

    private void initPackages(PropertySource propertySource) {
        if (propertySource.getProperty("beanlet.scan.base-package") != null) {
             basePackage = propertySource.getProperty("beanlet.scan.base-package");
        }else {
            basePackage = DEFAULT_BASE_PACKAGE;
        }
    }

    @Override
    public void scan(BeanDefinitionRegistry registry) throws ClassNotFoundException {
        File[] files = new File(basePackage).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scan(registry);
            } else if (file.getName().endsWith(".class")) {
                Class<?> clazz = ReflectionUtils.loadClass(file.getAbsolutePath());
                if (clazz.isAnnotationPresent(Component.class)){
                    BeanDefinition beanDefinition = componentExtractor.extract(clazz).getFirst();
                    registry.registerBeanDefinition(clazz.getName(), beanDefinition);
                    cacheInterfaceMappings(clazz,registry);
                }
                else if (clazz.isAnnotationPresent(Configuration.class)){
                    configurationExtractor.extract(clazz)
                            .forEach(beanDefinition -> {
                                registry.registerBeanDefinition(clazz.getName(), beanDefinition);
                                cacheInterfaceMappings(clazz,registry);
                            });
                }
            }
        }
    }

    private  void cacheInterfaceMappings(Class<?> clazz,BeanDefinitionRegistry registry) {
        String [] interfaces = ReflectionUtils.getAllInterfaces(clazz);
        for (String interfaceName : interfaces) {
            registry.addTypeInjectionCache(interfaceName, clazz.getName());
        }
    }
}
