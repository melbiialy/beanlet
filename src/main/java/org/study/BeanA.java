package org.study;

import org.study.beanlet.annotation.Bean;
import org.study.beanlet.annotation.Component;
import org.study.beanlet.annotation.Configuration;

@Configuration
public class BeanA {
    @Bean
    public BeanB beanB(){
        return new BeanB();
    }

}
