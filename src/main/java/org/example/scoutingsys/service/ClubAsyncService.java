package org.example.scoutingsys.service;

import org.example.scoutingsys.config.AsyncConfig;
import org.example.scoutingsys.dto.ClubFinancialAnalyticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ClubAsyncService {

    private static final Logger log = LoggerFactory.getLogger(ClubAsyncService.class);

    private final AtomicReference<String> lastBoardNotificationThread = new AtomicReference<>();
    private final AtomicReference<String> lastSquadValuationThread = new AtomicReference<>();

    @Async(AsyncConfig.SCOUTING_EXECUTOR)
    public void notifyClubBoard(Long clubId, String boardEmail) {
        String threadName = Thread.currentThread().getName();
        lastBoardNotificationThread.set(threadName);
        log.info("[Club-Async-Notify] Sending board alert for club {} to {} on thread {}",
                clubId, boardEmail, threadName);

        simulateDelay(200);

        if (boardEmail != null && boardEmail.contains("fail")) {
            throw new IllegalStateException("SMTP board delivery failed for email: " + boardEmail);
        }

        log.info("[Club-Async-Notify] Board alert delivered successfully for club {}", clubId);
    }

    @Async(AsyncConfig.SCOUTING_EXECUTOR)
    public CompletableFuture<BigDecimal> fetchSquadMarketValuation(Long clubId) {
        String threadName = Thread.currentThread().getName();
        lastSquadValuationThread.set(threadName);
        log.info("[Club-Async-Valuation] Calculating squad valuation for club {} on thread {}", clubId, threadName);

        if (clubId != null && clubId.equals(888L)) {
            simulateDelay(1500); // Trigger timeout
        } else {
            simulateDelay(300);
        }

        if (clubId != null && clubId.equals(999L)) {
            throw new RuntimeException("External valuation provider failed for club " + clubId);
        }

        BigDecimal base = BigDecimal.valueOf(50_000_000);
        BigDecimal value = base.add(BigDecimal.valueOf(clubId != null ? clubId * 5_000_000 : 0));
        return CompletableFuture.completedFuture(value);
    }

    @Async(AsyncConfig.SCOUTING_EXECUTOR)
    public CompletableFuture<ClubFinancialAnalyticsDto> evaluateFinancialFairPlay(Long clubId) {
        log.info("[Club-Async-FFP] Calculating Financial Fair Play index for club {} on thread {}",
                clubId, Thread.currentThread().getName());

        simulateDelay(300);

        ClubFinancialAnalyticsDto analytics = new ClubFinancialAnalyticsDto(
                clubId,
                85,
                "COMPLIANT",
                "Healthy wage-to-revenue ratio: 58%, net transfer balance within UEFA FFP limits"
        );
        return CompletableFuture.completedFuture(analytics);
    }

    @Async(AsyncConfig.LIMITED_EXECUTOR)
    public CompletableFuture<String> runBatchFinancialAudit(Long clubId, long sleepMillis) {
        log.info("[Club-Async-Limited] Running audit for club {} on thread {}", clubId, Thread.currentThread().getName());
        simulateDelay(sleepMillis);
        return CompletableFuture.completedFuture("Club " + clubId + " audit finished");
    }

    private void simulateDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Async operation interrupted", e);
        }
    }

    public String getLastBoardNotificationThread() {
        return lastBoardNotificationThread.get();
    }

    public String getLastSquadValuationThread() {
        return lastSquadValuationThread.get();
    }
}
