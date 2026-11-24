package org.example.scoutingsys.analytics.messaging;

import java.time.Instant;

public record ClubChangedEvent(
        String eventId,
        String eventType,
        Long clubId,
        String clubName,
        Long leagueId,
        Instant occurredAt
) {
}
