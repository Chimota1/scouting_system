package org.example.scoutingsys.analytics.messaging;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClubEventConsumerTest {
    @Test
    void duplicateEventDoesNotChangeProcessedResult() {
        ClubEventConsumer consumer = new ClubEventConsumer();
        ClubChangedEvent event = new ClubChangedEvent(
                "event-123", "CREATED", 7L, "Dynamo", 2L, Instant.parse("2026-11-19T17:00:00Z"));

        consumer.handle(event);
        consumer.handle(event);

        assertEquals(1, consumer.getProcessedCount());
        assertEquals(event, consumer.getProcessedEvents().get("event-123"));
    }
}
