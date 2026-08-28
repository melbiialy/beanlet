package demo;


import org.study.beanlet.applicationevents.ApplicationEventPublisher;
import org.study.beanlet.applicationevents.Event;
import org.study.beanlet.applicationevents.EventRegistry;
import org.study.beanlet.context.ApplicationContext;
import org.study.beanlet.context.DefaultApplicationContext;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws Exception {

        try (DefaultApplicationContext applicationContext = new DefaultApplicationContext()) {
            applicationContext.refresh();
            EventRegistry eventRegistry = (EventRegistry) applicationContext.getBean("EventRegistry");
            Event event = new Event(String.class, System.out::println);
            eventRegistry.register(event);
            ApplicationEventPublisher applicationEventPublisher = (ApplicationEventPublisher) applicationContext.getBean("ApplicationEventPublisher");
            applicationEventPublisher.publish("hello world");
        }

    }
}
