package org.example.scoutingsys.analytics.messaging;

import org.example.scoutingsys.analytics.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClubEventConsumer {
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();
    private final Map<String, ClubChangedEvent> processedEvents = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handle(ClubChangedEvent event) {
        if (!processedEventIds.add(event.eventId())) {
            return;
        }
        processedEvents.put(event.eventId(), event);
    }

    public int getProcessedCount() {
        return processedEventIds.size();
    }

    public Map<String, ClubChangedEvent> getProcessedEvents() {
        return Map.copyOf(processedEvents);
    }
}
