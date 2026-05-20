package org.study;

import org.study.beanlet.annotation.*;

@Configuration
public class BeanA {
    @Bean
    public BeanB beanB(@Value("${name}") int name){
        System.out.println(name);
        return new BeanB();
    }


}
