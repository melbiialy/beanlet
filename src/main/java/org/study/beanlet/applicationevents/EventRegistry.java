package org.study.beanlet.applicationevents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventRegistry {
    private final Map<Class<?>, List<Event>> events;

    public EventRegistry() {
        this.events = new ConcurrentHashMap<>();
    }
    public List<Event> getEvents(Object event) {
        System.out.println(event.getClass());
        return events.get(event.getClass());
    }
    public void register(Event event) {
        events.computeIfAbsent(event.eventClass, k -> new CopyOnWriteArrayList<>()).add(event);    }

}
