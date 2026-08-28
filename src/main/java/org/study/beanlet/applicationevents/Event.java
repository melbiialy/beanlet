package org.study.beanlet.applicationevents;

import java.util.function.Consumer;

public class Event {
    Class<?> eventClass;
    Consumer<Object> consumer;

    public Event(Class<?> eventClass, Consumer<Object> consumer) {
        this.eventClass = eventClass;
        this.consumer = consumer;
    }
    public boolean support(Object event) {
        return eventClass.isAssignableFrom(event.getClass());
    }
    public void consume(Object event) {
        consumer.accept(event);
    }

}
