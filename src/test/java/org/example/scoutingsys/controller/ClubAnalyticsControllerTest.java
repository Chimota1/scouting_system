package org.example.scoutingsys.controller;

import org.example.scoutingsys.dto.ClubDossierDto;
import org.example.scoutingsys.dto.ClubFinancialAnalyticsDto;
import org.example.scoutingsys.service.ClubAnalyticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubAnalyticsControllerTest {

    @Mock
    private ClubAnalyticsService clubAnalyticsService;

    @InjectMocks
    private ClubAnalyticsController clubAnalyticsController;

    @Test
    @DisplayName("Отримання досьє клубу повертає HTTP 200 та об'єднаний результат")
    void getClubDossier_shouldReturnOkWithDossier() {
        Long clubId = 1L;
        ClubFinancialAnalyticsDto ffp = new ClubFinancialAnalyticsDto(clubId, 90, "COMPLIANT", "Wage ratio 55%");
        ClubDossierDto expectedDossier = new ClubDossierDto(clubId, BigDecimal.valueOf(60_000_000), ffp, "DOSSIER_READY");

        when(clubAnalyticsService.generateClubDossier(clubId))
                .thenReturn(CompletableFuture.completedFuture(expectedDossier));

        ResponseEntity<ClubDossierDto> response = clubAnalyticsController.getClubDossier(clubId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(clubId, response.getBody().getClubId());
        assertEquals("DOSSIER_READY", response.getBody().getStatus());
        assertEquals(BigDecimal.valueOf(60_000_000), response.getBody().getTotalSquadValuation());
        verify(clubAnalyticsService, times(1)).generateClubDossier(clubId);
    }

    @Test
    @DisplayName("Сповіщення ради директорів повертає HTTP 202 Accepted")
    void notifyBoard_shouldReturnAccepted() {
        Long clubId = 1L;
        String email = "board@dynamo.ua";

        ResponseEntity<String> response = clubAnalyticsController.notifyBoard(clubId, email);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Board notification scheduled", response.getBody());
        verify(clubAnalyticsService, times(1)).notifyBoard(clubId, email);
    }

    @Test
    @DisplayName("Запуск аудиту на обмеженому пулі повертає HTTP 200 із повідомленням")
    void runAudit_shouldReturnOk() {
        Long clubId = 1L;
        long sleepMillis = 200L;
        when(clubAnalyticsService.runAuditOnLimitedPool(clubId, sleepMillis))
                .thenReturn("Club 1 audit finished");

        ResponseEntity<String> response = clubAnalyticsController.runAudit(clubId, sleepMillis);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Club 1 audit finished", response.getBody());
        verify(clubAnalyticsService, times(1)).runAuditOnLimitedPool(clubId, sleepMillis);
    }
}
