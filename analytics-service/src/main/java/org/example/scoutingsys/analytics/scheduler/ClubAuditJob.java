package org.example.scoutingsys.analytics.scheduler;

import org.example.scoutingsys.analytics.service.ScheduledScoutingService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class ClubAuditJob implements Job {

    private final ScheduledScoutingService service;

    public ClubAuditJob(ScheduledScoutingService service) {
        this.service = service;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getMergedJobDataMap();
        long clubId = ClubReportJob.requiredLong(data, "clubId");
        long durationMillis = ClubReportJob.requiredLong(data, "durationMillis");

        try {
            service.runClubAudit(clubId, durationMillis);
        } catch (RuntimeException exception) {
            throw new JobExecutionException(
                    "Club audit failed for club " + clubId, exception, false);
        }
    }
}
