package demo;

import org.study.beanlet.annotation.Bean;
import org.study.beanlet.annotation.Configuration;

@Configuration
public class Configration {

    @Bean
    public SampleBean sampleBean() {
        SampleBean sampleBean = new SampleBean();
        System.out.println(sampleBean);
        return sampleBean;
    }
}
