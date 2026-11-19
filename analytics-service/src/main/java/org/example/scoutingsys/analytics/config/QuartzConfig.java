package org.example.scoutingsys.analytics.config;

import org.example.scoutingsys.analytics.scheduler.ClubAuditJob;
import org.example.scoutingsys.analytics.scheduler.ClubReportJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class QuartzConfig {

    public static final String GROUP = "scouting";
    private static final String REPORT_JOB = "club-report-job";
    private static final String AUDIT_JOB = "club-audit-job";
    private static final TimeZone KYIV = TimeZone.getTimeZone("Europe/Kyiv");

    @Bean
    public JobDetail clubReportJobDetail() {
        JobDataMap defaults = new JobDataMap();
        defaults.put("clubId", 1L);
        defaults.put("recipient", "board@club.com");
        defaults.put("timeoutMillis", 30_000L);

        return JobBuilder.newJob(ClubReportJob.class)
                .withIdentity(REPORT_JOB, GROUP)
                .withDescription("Generate a club scouting dossier")
                .usingJobData(defaults)
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    public CronTrigger morningClubReportTrigger(JobDetail clubReportJobDetail) {
        JobDataMap overrides = new JobDataMap();
        overrides.put("recipient", "morning-reports@club.com");

        return TriggerBuilder.newTrigger()
                .withIdentity("club-report-morning-trigger", GROUP)
                .forJob(clubReportJobDetail.getKey())
                .usingJobData(overrides)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 8 * * ?")
                        .inTimeZone(KYIV)
                        .withMisfireHandlingInstructionDoNothing())
                .build();
    }

    @Bean
    public CronTrigger weeklyClubReportTrigger(JobDetail clubReportJobDetail) {
        JobDataMap overrides = new JobDataMap();
        overrides.put("recipient", "weekly-reports@club.com");
        overrides.put("timeoutMillis", 60_000L);

        return TriggerBuilder.newTrigger()
                .withIdentity("club-report-weekly-trigger", GROUP)
                .forJob(clubReportJobDetail.getKey())
                .usingJobData(overrides)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 9 ? * MON")
                        .inTimeZone(KYIV)
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }

    @Bean
    public JobDetail clubAuditJobDetail() {
        JobDataMap defaults = new JobDataMap();
        defaults.put("clubId", 1L);
        defaults.put("durationMillis", 100L);

        return JobBuilder.newJob(ClubAuditJob.class)
                .withIdentity(AUDIT_JOB, GROUP)
                .withDescription("Run a periodic club financial audit")
                .usingJobData(defaults)
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    public Trigger clubAuditTrigger(JobDetail clubAuditJobDetail) {
        return TriggerBuilder.newTrigger()
                .withIdentity("club-audit-hourly-trigger", GROUP)
                .forJob(clubAuditJobDetail.getKey())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(1)
                        .repeatForever()
                        .withMisfireHandlingInstructionFireNow())
                .startNow()
                .build();
    }
}
