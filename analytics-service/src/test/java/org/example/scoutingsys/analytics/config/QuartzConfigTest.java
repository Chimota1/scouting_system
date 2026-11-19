package org.example.scoutingsys.analytics.config;

import org.example.scoutingsys.analytics.scheduler.ClubReportJob;
import org.junit.jupiter.api.Test;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuartzConfigTest {

    private final QuartzConfig config = new QuartzConfig();

    @Test
    void triggersUseTheExpectedJobsAndGroups() {
        // Arrange
        JobDetail detail = config.clubReportJobDetail();

        // Act
        CronTrigger morning = config.morningClubReportTrigger(detail);
        CronTrigger weekly = config.weeklyClubReportTrigger(detail);

        // Assert
        assertEquals(new JobKey("club-report-job", "scouting"), detail.getKey());
        assertEquals(detail.getKey(), morning.getJobKey());
        assertEquals(detail.getKey(), weekly.getJobKey());
        assertEquals("scouting", morning.getKey().getGroup());
        assertEquals("scouting", weekly.getKey().getGroup());
    }

    @Test
    void triggerDataOverridesDefaultsAndKeepsOtherDefaults() {
        // Arrange
        JobDetail detail = config.clubReportJobDetail();
        CronTrigger trigger = config.morningClubReportTrigger(detail);

        // Act
        JobDataMap merged = new JobDataMap(detail.getJobDataMap());
        merged.putAll(trigger.getJobDataMap());

        // Assert
        assertEquals("morning-reports@club.com", merged.getString("recipient"));
        assertEquals(1L, merged.getLong("clubId"));
        assertEquals(30_000L, merged.getLong("timeoutMillis"));
    }

    @Test
    void cronTriggersDeclareMisfireAndTimeZone() {
        // Arrange
        JobDetail detail = config.clubReportJobDetail();

        // Act
        CronTrigger morning = config.morningClubReportTrigger(detail);
        CronTrigger weekly = config.weeklyClubReportTrigger(detail);

        // Assert
        assertEquals(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING,
                morning.getMisfireInstruction());
        assertEquals(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW,
                weekly.getMisfireInstruction());
        assertEquals("Europe/Kyiv", morning.getTimeZone().getID());
        assertEquals("Europe/Kyiv", weekly.getTimeZone().getID());
        assertEquals("GMT", TimeZone.getTimeZone("not-a-zone").getID());
    }

    @Test
    void reportJobIsNonConcurrent() {
        // Arrange
        Class<ClubReportJob> jobClass = ClubReportJob.class;

        // Act
        boolean nonConcurrent = jobClass.isAnnotationPresent(
                org.quartz.DisallowConcurrentExecution.class);

        // Assert
        assertTrue(nonConcurrent);
    }
}
