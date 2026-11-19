package org.example.scoutingsys.analytics.service;

import org.example.scoutingsys.analytics.dto.ClubDossierDto;
import org.example.scoutingsys.analytics.dto.ClubFinancialAnalyticsDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskRejectedException;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubAnalyticsServiceTest {

    @Mock
    private ClubAsyncService clubAsyncService;

    @InjectMocks
    private ClubAnalyticsService clubAnalyticsService;

    @Test
    @DisplayName("Сценарій 1: notifyBoard() делегує виклик у ClubAsyncService без блокування")
    void notifyBoard_shouldDelegateToAsyncService() {
        // Arrange
        Long clubId = 1L;
        String email = "board@dynamo.ua";

        // Act
        clubAnalyticsService.notifyBoard(clubId, email);

        // Assert
        verify(clubAsyncService, times(1)).notifyClubBoard(clubId, email);
    }

    @Test
    @DisplayName("Сценарій 3: generateClubDossier() об'єднує результати двох асинхронних викликів через thenCombine")
    void generateClubDossier_shouldCombineFuturesUsingThenCombine() {
        // Arrange
        Long clubId = 3L;
        BigDecimal expectedValuation = BigDecimal.valueOf(65_000_000);
        ClubFinancialAnalyticsDto expectedFfp = new ClubFinancialAnalyticsDto(
                clubId, 85, "COMPLIANT", "Healthy ratio"
        );

        when(clubAsyncService.fetchSquadMarketValuation(clubId))
                .thenReturn(CompletableFuture.completedFuture(expectedValuation));
        when(clubAsyncService.evaluateFinancialFairPlay(clubId))
                .thenReturn(CompletableFuture.completedFuture(expectedFfp));

        // Act
        ClubDossierDto dossier = clubAnalyticsService.generateClubDossier(clubId).join();

        // Assert
        assertNotNull(dossier);
        assertEquals(clubId, dossier.getClubId());
        assertEquals("DOSSIER_READY", dossier.getStatus());
        assertEquals(expectedValuation, dossier.getTotalSquadValuation());
        assertEquals(expectedFfp, dossier.getFinancialAnalytics());
        verify(clubAsyncService, times(1)).fetchSquadMarketValuation(clubId);
        verify(clubAsyncService, times(1)).evaluateFinancialFairPlay(clubId);
    }

    @Test
    @DisplayName("Сценарій 4: Збій у fetchSquadMarketValuation перехоплюється exceptionally і повертає fallback")
    void generateClubDossier_whenValuationFails_shouldApplyFallback() {
        // Arrange
        Long clubId = 999L;
        ClubFinancialAnalyticsDto expectedFfp = new ClubFinancialAnalyticsDto(
                clubId, 80, "COMPLIANT", "Normal"
        );

        when(clubAsyncService.fetchSquadMarketValuation(clubId))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("External API unavailable")));
        when(clubAsyncService.evaluateFinancialFairPlay(clubId))
                .thenReturn(CompletableFuture.completedFuture(expectedFfp));

        // Act
        ClubDossierDto dossier = clubAnalyticsService.generateClubDossier(clubId).join();

        // Assert
        assertNotNull(dossier);
        assertEquals(clubId, dossier.getClubId());
        assertEquals(BigDecimal.valueOf(10_000_000), dossier.getTotalSquadValuation());
        assertEquals(expectedFfp, dossier.getFinancialAnalytics());
    }

    @Test
    @DisplayName("Сценарій 5: getValuationWithTimeout() повертає результат при успішному виконанні до настання таймауту")
    void getValuationWithTimeout_whenSuccessful_shouldReturnValue() {
        // Arrange
        Long clubId = 1L;
        BigDecimal expectedValuation = BigDecimal.valueOf(55_000_000);
        when(clubAsyncService.fetchSquadMarketValuation(clubId))
                .thenReturn(CompletableFuture.completedFuture(expectedValuation));

        // Act
        BigDecimal result = clubAnalyticsService.getValuationWithTimeout(clubId, 1000).join();

        // Assert
        assertEquals(expectedValuation, result);
        verify(clubAsyncService, times(1)).fetchSquadMarketValuation(clubId);
    }

    @Test
    @DisplayName("Сценарій 5: getValuationWithTimeout() повертає маркер -1 при таймауті або збої")
    void getValuationWithTimeout_whenFails_shouldReturnFallback() {
        // Arrange
        Long clubId = 888L;
        when(clubAsyncService.fetchSquadMarketValuation(clubId))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Timeout simulated")));

        // Act
        BigDecimal result = clubAnalyticsService.getValuationWithTimeout(clubId, 100).join();

        // Assert
        assertEquals(BigDecimal.valueOf(-1), result);
        verify(clubAsyncService, times(1)).fetchSquadMarketValuation(clubId);
    }

    @Test
    @DisplayName("Сценарій 6: runAuditOnLimitedPool() повертає результат успішної задачі")
    void runAuditOnLimitedPool_whenSuccessful_shouldReturnResult() {
        // Arrange
        Long clubId = 1L;
        long sleepMillis = 200L;
        when(clubAsyncService.runBatchFinancialAudit(clubId, sleepMillis))
                .thenReturn(CompletableFuture.completedFuture("Club 1 audit finished"));

        // Act
        String result = clubAnalyticsService.runAuditOnLimitedPool(clubId, sleepMillis);

        // Assert
        assertEquals("Club 1 audit finished", result);
        verify(clubAsyncService, times(1)).runBatchFinancialAudit(clubId, sleepMillis);
    }

    @Test
    @DisplayName("Сценарій 6: runAuditOnLimitedPool() перехоплює TaskRejectedException і повертає коректне повідомлення")
    void runAuditOnLimitedPool_whenTaskRejected_shouldReturnGracefulMessage() {
        // Arrange
        Long clubId = 2L;
        long sleepMillis = 500L;
        when(clubAsyncService.runBatchFinancialAudit(clubId, sleepMillis))
                .thenThrow(new TaskRejectedException("Queue full"));

        // Act
        String result = clubAnalyticsService.runAuditOnLimitedPool(clubId, sleepMillis);

        // Assert
        assertEquals("Club audit queue is full, please try again later", result);
        verify(clubAsyncService, times(1)).runBatchFinancialAudit(clubId, sleepMillis);
    }
}
