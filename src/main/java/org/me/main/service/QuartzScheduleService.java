package org.me.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzScheduleService {
    private final Scheduler scheduler;

    public void createEmailJob(Long scheduleId, String cron, String to, String subject, String body) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(EmailJobService.class)
                .withIdentity("emailJob-" + scheduleId, "email-jobs")
                .usingJobData("receiverEmail", to)
                .usingJobData("subject", subject)
                .usingJobData("body", body)
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("emailTrigger-" + scheduleId, "email-triggers")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)
                        .inTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")))
                .forJob(jobDetail)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        logJob("Created", jobDetail, trigger);
    }

    public void updateEmailJob(Long scheduleId, String cron, String to, String subject, String body) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey("emailJob-" + scheduleId, "email-jobs");
        TriggerKey triggerKey = TriggerKey.triggerKey("emailTrigger-" + scheduleId, "email-triggers");

        if (!scheduler.checkExists(jobKey)) {
            createEmailJob(scheduleId, cron, to, subject, body);
            return;
        }

        JobDetail jobDetail = scheduler.getJobDetail(jobKey)
                .getJobBuilder()
                .usingJobData("receiverEmail", to)
                .usingJobData("subject", subject)
                .usingJobData("body", body)
                .build();

        CronTrigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)
                        .inTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")))
                .forJob(jobDetail)
                .build();

        scheduler.addJob(jobDetail, true, true);
        scheduler.rescheduleJob(triggerKey, newTrigger);
        logJob("Updated", jobDetail, newTrigger);
    }

    public void deleteEmailJob(Long scheduleId) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey("emailJob-" + scheduleId, "email-jobs");
        if (!scheduler.checkExists(jobKey)) {
            throw new SchedulerException("Job không tồn tại để xóa");
        }
        scheduler.deleteJob(jobKey);
        log.info("Deleted job: {}", jobKey);
    }

    private void logJob(String action, JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        Trigger.TriggerState state = scheduler.getTriggerState(trigger.getKey());
        log.info("{} job: {} | Trigger: {} | State: {}",
                action, jobDetail.getKey(), trigger.getKey(), state);
    }

    public void printAllJobs() throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                log.info("Job: {}", jobKey);
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    log.info("  Trigger: {} - state: {}", trigger.getKey(), scheduler.getTriggerState(trigger.getKey()));
                }
            }
        }
    }
}
