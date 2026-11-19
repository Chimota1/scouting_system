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
public class ClubReportJob implements Job {

    private final ScheduledScoutingService service;

    public ClubReportJob(ScheduledScoutingService service) {
        this.service = service;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getMergedJobDataMap();
        long clubId = requiredLong(data, "clubId");
        String recipient = requiredString(data, "recipient");
        long timeoutMillis = requiredLong(data, "timeoutMillis");

        try {
            service.generateClubReport(clubId, recipient, timeoutMillis);
        } catch (RuntimeException exception) {
            throw new JobExecutionException(
                    "Club report failed for club " + clubId, exception, false);
        }
    }

    public static long requiredLong(JobDataMap data, String key)
            throws JobExecutionException {
        Object value = data.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        throw new JobExecutionException("Missing or invalid Quartz parameter: " + key);
    }

    public static String requiredString(JobDataMap data, String key)
            throws JobExecutionException {
        String value = data.getString(key);
        if (value == null || value.isBlank()) {
            throw new JobExecutionException("Missing or invalid Quartz parameter: " + key);
        }
        return value;
    }
}
