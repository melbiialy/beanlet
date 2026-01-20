package org.study.beanlet.context;

import org.study.exception.BeanScanningException;
import org.study.exception.InitializationException;
import org.study.beanlet.beans.factory.BeanFactory;
import org.study.beanlet.beans.factory.DefaultBeanFactory;
import org.study.beanlet.beans.factory.support.BeanDefinitionRegistry;
import org.study.beanlet.core.scanning.ComponentScanner;
import org.study.beanlet.env.PropertySource;
import org.study.beanlet.env.PropertySourceLoader;
import org.study.beanlet.env.YamlPropertySourceLoader;


import java.lang.reflect.InvocationTargetException;

public class DefaultApplicationContext implements ApplicationContext{
    
    private BeanFactory beanFactory;

    @Override
    public Object getBean(String beanName) throws InvocationTargetException, InstantiationException, IllegalAccessException {
       return beanFactory.getBean(beanName);
    }

    @Override
    public Object getQualifiedBean(String beanName, String value) throws InvocationTargetException, InstantiationException, IllegalAccessException {
      return  beanFactory.getQualifiedBean(beanName,value);
    }

    @Override
    public void refresh() {
        PropertySource properties = getPropertySource();
        BeanDefinitionRegistry registry = getBeanDefinitionRegistry(properties);
        beanFactory = new DefaultBeanFactory(registry,properties);
        preInitializeBeans(registry);
    }

    private void preInitializeBeans(BeanDefinitionRegistry registry) {
        for (String beanName : registry.getBeanNames()) {
            try {
                getBean(beanName);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new InitializationException(e.getMessage());
            }
        }
    }

    private static BeanDefinitionRegistry getBeanDefinitionRegistry(PropertySource properties){
        ComponentScanner componentScanner = new ComponentScanner();
        String basePackage = properties.getProperty("spring.main.base-package");
        if (basePackage != null) {
            componentScanner.setBasePackage(basePackage);
        }
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        try {
            componentScanner.scan(registry);
            return registry;
        } catch (ClassNotFoundException e) {
            throw new BeanScanningException(e.getMessage());

        }
    }

    private static PropertySource getPropertySource() {
        PropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader();
        return propertySourceLoader.loadProperties("application.yml");
    }
}
