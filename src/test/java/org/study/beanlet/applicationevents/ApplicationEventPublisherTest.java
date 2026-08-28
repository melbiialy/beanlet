package org.study.beanlet.applicationevents;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationEventPublisherTest {

    @Test
    void publishesEventsToSubscribersOfTheConcreteAndParentTypes() throws Exception {
        EventRegistry registry = new EventRegistry();
        CountDownLatch handled = new CountDownLatch(2);
        registry.register(new Event(BaseEvent.class, event -> handled.countDown()));
        registry.register(new Event(UserCreated.class, event -> handled.countDown()));

        try (ApplicationEventPublisher publisher = new ApplicationEventPublisher(registry)) {
            publisher.publish(new UserCreated());
            assertTrue(handled.await(2, TimeUnit.SECONDS));
        }
    }

    static class BaseEvent { }
    static class UserCreated extends BaseEvent { }
}
