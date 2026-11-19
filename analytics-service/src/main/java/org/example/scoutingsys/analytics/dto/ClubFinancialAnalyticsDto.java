package org.example.scoutingsys.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubFinancialAnalyticsDto {
    private Long clubId;
    private int financialFairPlayScore;
    private String complianceStatus;
    private String wageBudgetSummary;
}
