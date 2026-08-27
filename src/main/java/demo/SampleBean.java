package demo;

import org.study.beanlet.annotation.PreDestroy;
import org.study.beanlet.annotation.PostConstruct;

public class SampleBean {
    @PostConstruct
    public void init(){
        System.out.println("init");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("destroy");
    }
}
