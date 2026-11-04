package org.example.scoutingsys.service;

import org.example.scoutingsys.dto.ClubDossierDto;
import org.example.scoutingsys.dto.ClubFinancialAnalyticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@Service
public class ClubAnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(ClubAnalyticsService.class);

    private final ClubAsyncService clubAsyncService;

    public ClubAnalyticsService(ClubAsyncService clubAsyncService) {
        this.clubAsyncService = clubAsyncService;
    }

    public void notifyBoard(Long clubId, String boardEmail) {
        clubAsyncService.notifyClubBoard(clubId, boardEmail);
    }


    public CompletableFuture<ClubDossierDto> generateClubDossier(Long clubId) {
        log.info("Starting parallel dossier generation for club {}", clubId);

        // Повільний виклик 1 із захистом від падіння через exceptionally
        CompletableFuture<BigDecimal> valuationFuture = clubAsyncService.fetchSquadMarketValuation(clubId)
                .exceptionally(ex -> {
                    log.warn("Valuation lookup failed for club {}, using minimum baseline fallback: {}",
                            clubId, ex.getMessage());
                    return BigDecimal.valueOf(10_000_000); // Резервне значення за замовчуванням
                });

        // Повільний виклик 2
        CompletableFuture<ClubFinancialAnalyticsDto> ffpFuture = clubAsyncService.evaluateFinancialFairPlay(clubId);

        // thenCombine об'єднує результати двох одночасних операцій
        return valuationFuture.thenCombine(ffpFuture, (valuation, ffpAnalytics) -> {
            log.info("Merging valuation ({}) and FFP score ({}) for club {}",
                    valuation, ffpAnalytics.getFinancialFairPlayScore(), clubId);
            return new ClubDossierDto(
                    clubId,
                    valuation,
                    ffpAnalytics,
                    "DOSSIER_READY"
            );
        });
    }

    public CompletableFuture<BigDecimal> getValuationWithTimeout(Long clubId, long timeoutMillis) {
        return clubAsyncService.fetchSquadMarketValuation(clubId)
                .orTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> {
                    log.warn("Club valuation timed out or failed after {}ms: {}", timeoutMillis, ex.getMessage());
                    return BigDecimal.valueOf(-1);
                });
    }

    public String runAuditOnLimitedPool(Long clubId, long sleepMillis) {
        try {
            return clubAsyncService.runBatchFinancialAudit(clubId, sleepMillis).join();
        } catch (TaskRejectedException ex) {
            log.warn("Immediate TaskRejectedException for club {}: {}", clubId, ex.getMessage());
            return "Club audit queue is full, please try again later";
        } catch (CompletionException ce) {
            if (ce.getCause() instanceof TaskRejectedException) {
                log.warn("Nested TaskRejectedException for club {}: {}", clubId, ce.getCause().getMessage());
                return "Club audit queue is full, please try again later";
            }
            throw ce;
        }
    }
}
