package org.example.scoutingsys.analytics.scheduler;

import org.example.scoutingsys.analytics.service.ScheduledScoutingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClubReportJobTest {

    @Mock
    private ScheduledScoutingService service;

    @Mock
    private JobExecutionContext context;

    @Test
    void jobReadsMergedDataAndCallsOneSpringService() throws Exception {
        // Arrange
        JobDataMap data = new JobDataMap();
        data.put("clubId", 7L);
        data.put("recipient", "reports@club.com");
        data.put("timeoutMillis", 10_000L);
        when(context.getMergedJobDataMap()).thenReturn(data);

        // Act
        new ClubReportJob(service).execute(context);

        // Assert
        verify(service).generateClubReport(7L, "reports@club.com", 10_000L);
    }

    @Test
    void missingParameterProducesExplicitFailure() {
        // Arrange
        when(context.getMergedJobDataMap()).thenReturn(new JobDataMap());

        // Act
        JobExecutionException exception = assertThrows(
                JobExecutionException.class,
                () -> new ClubReportJob(service).execute(context));

        // Assert
        assertEquals("Missing or invalid Quartz parameter: clubId",
                exception.getMessage());
    }

    @Test
    void serviceFailureIsRecordedWithoutInfiniteRefire() {
        // Arrange
        JobDataMap data = new JobDataMap();
        data.put("clubId", 7L);
        data.put("recipient", "reports@club.com");
        data.put("timeoutMillis", 10_000L);
        when(context.getMergedJobDataMap()).thenReturn(data);
        org.mockito.Mockito.doThrow(new IllegalStateException("temporary failure"))
                .when(service).generateClubReport(7L, "reports@club.com", 10_000L);

        // Act
        JobExecutionException exception = assertThrows(
                JobExecutionException.class,
                () -> new ClubReportJob(service).execute(context));

        // Assert
        assertEquals(false, exception.refireImmediately());
    }
}
