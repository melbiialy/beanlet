package demo;

import org.study.beanlet.annotation.*;

@Configuration
public class SampleConfiguration {
    @Bean
    public SampleBean beanB(@Value("${name}") int name){
        System.out.println(name);
        return new SampleBean();
    }


}
