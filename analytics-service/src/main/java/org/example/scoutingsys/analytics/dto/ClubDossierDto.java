package org.example.scoutingsys.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubDossierDto {
    private Long clubId;
    private BigDecimal totalSquadValuation;
    private ClubFinancialAnalyticsDto financialAnalytics;
    private String status;
}
