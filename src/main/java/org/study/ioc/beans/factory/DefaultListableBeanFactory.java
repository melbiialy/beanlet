package org.study.ioc.beans.factory;

import org.study.ioc.beans.defintion.BeanDefinition;
import org.study.ioc.beans.defintion.BeanScope;
import org.study.ioc.beans.factory.support.BeanDefinitionRegistry;
import org.study.ioc.beans.factory.support.SingletonBeanRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory{
    private final BeanDefinitionRegistry registry;
    private final SingletonBeanRegistry singletonBeanRegistry;
    public DefaultListableBeanFactory(BeanDefinitionRegistry registry) {
        this.registry = registry;
        singletonBeanRegistry = new SingletonBeanRegistry();
    }
    @Override
    public Object getBean(String beanName) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
        if (beanDefinition == null){
            throw new RuntimeException("No such bean");
        }
        if (beanDefinition.getBeanScope() == BeanScope.PROTOTYPE){
            return createBean(beanName,beanDefinition);
        }
       if (singletonBeanRegistry.containsSingleton(beanName)){
           return singletonBeanRegistry.getSingleton(beanName);
       }
       if (!registry.containsBeanDefinition(beanName)){
           throw new RuntimeException("No such manage bean");
       }
       Object bean = createBean(beanName,beanDefinition);
       singletonBeanRegistry.registerSingleton(beanName,bean);
       return bean;
    }
    public void preInstantiateSingletons(){
        List<String > beanNames = registry.getBeanNames();
        beanNames.forEach(beanName -> {
            try {
                getBean(beanName);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
