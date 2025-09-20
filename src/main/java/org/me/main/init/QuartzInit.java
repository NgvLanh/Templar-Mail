package org.me.main.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.me.main.model.Schedule;
import org.me.main.model._enum.Status;
import org.me.main.repo.ScheduleRepo;
import org.me.main.service.QuartzScheduleService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzInit implements ApplicationRunner {
    private final ScheduleRepo scheduleRepo;
    private final QuartzScheduleService quartzService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("*** QuartzInit loading ACTIVE schedules...");
        log.info("*** Total active schedules = {}", scheduleRepo.findByStatus(Status.ACTIVE).size());

        for (Schedule s : scheduleRepo.findByStatus(Status.ACTIVE)) {
            quartzService.createEmailJob(
                    s.getId(),
                    s.getCronExpression(),
                    s.getReceiverEmail(),
                    s.getName(),
                    s.getEmailTemplate().getBody()
            );
        }
    }
}
