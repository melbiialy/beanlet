package demo;

import org.study.beanlet.annotation.*;

@Component
public class SampleConfiguration {
    private final SampleBean  sampleBean;
    public SampleConfiguration(SampleBean sampleBean) {
        this.sampleBean = sampleBean;
        System.out.println(sampleBean);
    }



}
