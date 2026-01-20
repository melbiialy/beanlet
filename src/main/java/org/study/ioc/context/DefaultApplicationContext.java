package org.study.ioc.context;

import org.study.ioc.beans.factory.BeanFactory;
import org.study.ioc.beans.factory.DefaultBeanFactory;
import org.study.ioc.beans.factory.support.BeanDefinitionRegistry;
import org.study.ioc.componentscan.ComponentScanner;
import org.study.ioc.property.PropertySource;
import org.study.ioc.property.PropertySourceLoader;
import org.study.ioc.property.YamlPropertySourceLoader;


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
    public void refresh() throws ClassNotFoundException {
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
                throw new RuntimeException(e);
            }
        }
    }

    private static BeanDefinitionRegistry getBeanDefinitionRegistry(PropertySource properties) throws ClassNotFoundException {
        ComponentScanner componentScanner = new ComponentScanner();
        String basePackage = properties.getProperty("spring.main.base-package");
        if (basePackage != null) {
            componentScanner.setBasePackage(basePackage);
        }
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        componentScanner.scan(registry);
        return registry;
    }

    private static PropertySource getPropertySource() {
        PropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader();
        return propertySourceLoader.loadProperties("application.yml");
    }
}
