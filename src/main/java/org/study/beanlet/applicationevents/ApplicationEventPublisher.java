package org.study.beanlet.applicationevents;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class ApplicationEventPublisher implements Closeable {

    private final EventRegistry eventRegistry;
    private final ExecutorService executor;

    public ApplicationEventPublisher(EventRegistry eventRegistry) {
        this.eventRegistry = eventRegistry;

        this.executor = new ThreadPoolExecutor(
                0,
                100,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(500),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void publish(Object event) {
        System.out.println("publish event:"+event);
        List<Event> events = eventRegistry.getEvents(event);

        if (events == null) {
            return;
        }

        for (Event ev : events) {
            executor.submit(() -> ev.consume(event));
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() throws IOException {
        shutdown();
    }
}