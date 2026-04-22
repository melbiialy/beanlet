package org.study;

import org.study.beanlet.annotation.Bean;
import org.study.beanlet.annotation.Component;
import org.study.beanlet.annotation.Configuration;
import org.study.beanlet.annotation.Value;

@Configuration
public class BeanA {
    @Bean
    public BeanB beanB(@Value("${name}") int name){
        System.out.println(name);
        return new BeanB();
    }

}
