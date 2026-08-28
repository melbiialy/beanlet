package demo;

import org.study.beanlet.annotation.Autowired;
import org.study.beanlet.annotation.Component;
import org.study.beanlet.annotation.PreDestroy;
import org.study.beanlet.annotation.PostConstruct;

public class SampleBean {
    public SampleBean() {
        System.out.println(this);
    }
}
