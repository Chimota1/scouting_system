package org.example.scoutingsys.analytics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledScoutingService {

    private final ClubAnalyticsService clubAnalyticsService;

    public ScheduledScoutingService(ClubAnalyticsService clubAnalyticsService) {
        this.clubAnalyticsService = clubAnalyticsService;
    }

    @Transactional(readOnly = true)
    public void generateClubReport(Long clubId, String recipient, long timeoutMillis) {
        requirePositive(clubId, "clubId");
        requireText(recipient, "recipient");
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("timeoutMillis must be positive");
        }
        clubAnalyticsService.generateClubDossier(clubId)
                .orTimeout(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
                .join();
    }

    @Transactional(readOnly = true)
    public void runClubAudit(Long clubId, long durationMillis) {
        requirePositive(clubId, "clubId");
        if (durationMillis < 0) {
            throw new IllegalArgumentException("durationMillis must not be negative");
        }
        clubAnalyticsService.runAuditOnLimitedPool(clubId, durationMillis);
    }

    private void requirePositive(Long value, String name) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    private void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
