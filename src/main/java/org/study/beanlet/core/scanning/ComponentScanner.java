package org.study.beanlet.core.scanning;

import org.study.beanlet.beans.factory.support.BeanDefinitionRegistry;
import org.study.beanlet.env.PropertySource;

import java.util.ArrayList;
import java.util.List;


public class ComponentScanner implements Scanner{
    private final List<String> packagesToScan;
    private final String BASE_PACKAGE = "";

    public ComponentScanner(PropertySource propertySource) {
        this.packagesToScan = new ArrayList<>();
        initPackages(propertySource);
    }

    private void initPackages(PropertySource propertySource) {
        if (propertySource.getProperty("beanlet.scan.base-package") != null) {
            packagesToScan.add(propertySource.getProperty("beanlet.scan.base-package"));
        }else {
            packagesToScan.add(BASE_PACKAGE);
        }
        if (propertySource.getProperty("beanlet.scan.additional-base-packages") != null) {
            packagesToScan.addAll(propertySource.getAll("beanlet.scan.additional-base-packages"));
        }
    }

    @Override
    public void scan(BeanDefinitionRegistry registry) {

    }
}
