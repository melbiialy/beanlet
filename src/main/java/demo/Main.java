package demo;


import org.study.beanlet.context.ApplicationContext;
import org.study.beanlet.context.DefaultApplicationContext;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {

        ApplicationContext applicationContext = new DefaultApplicationContext();
        applicationContext.refresh();


    }
}
