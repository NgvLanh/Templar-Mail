package org.me.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.me.main.dto.req.ScheduleReq;
import org.me.main.dto.res.ApiRes;
import org.me.main.mapper.ScheduleMapper;
import org.me.main.model.EmailTemplate;
import org.me.main.model.Schedule;
import org.me.main.model._enum.Status;
import org.me.main.repo.ScheduleRepo;
import org.me.main.repo.TemplateRepo;
import org.me.main.service._interface.IScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleService implements IScheduleService {
    private final ScheduleRepo scheduleRepo;
    private final ScheduleMapper scheduleMapper;
    private final TemplateRepo templateRepo;
    private final QuartzScheduleService quartzService;


    @Override
    public ResponseEntity<?> scheduleOptions(ScheduleReq req) {
        String method = req.getMethod();
        try {
            switch (method) {
                case "1" -> {
                    Schedule schedule = new Schedule();
                    schedule.setName(req.getName());
                    schedule.setCronExpression(req.getCronExpression());
                    schedule.setReceiverEmail(req.getReceiverEmail());
                    schedule.setStatus(req.getStatus());
                    schedule.setType(req.getType());

                    EmailTemplate template = templateRepo.findById(req.getTemplateId())
                            .orElseThrow(() -> new RuntimeException("Template không tồn tại"));
                    schedule.setEmailTemplate(template);

                    scheduleRepo.save(schedule);

                    if (schedule.getStatus().equals(Status.ACTIVE)) {
                        quartzService.createEmailJob(
                                schedule.getId(),
                                schedule.getCronExpression(),
                                schedule.getReceiverEmail(),
                                schedule.getName(),
                                schedule.getEmailTemplate().getBody()
                        );
                    }
                }
                case "0" -> {
                    Schedule schedule = scheduleRepo.findById(req.getId())
                            .orElseThrow(() -> new RuntimeException("Schedule không tồn tại"));

                    Status oldStatus = schedule.getStatus();

                    schedule.setName(req.getName());
                    schedule.setCronExpression(req.getCronExpression());
                    schedule.setReceiverEmail(req.getReceiverEmail());
                    schedule.setStatus(req.getStatus());
                    schedule.setType(req.getType());

                    EmailTemplate template = templateRepo.findById(req.getTemplateId())
                            .orElseThrow(() -> new RuntimeException("Template không tồn tại"));
                    schedule.setEmailTemplate(template);

                    scheduleRepo.save(schedule);

                    if (oldStatus == Status.INACTIVE && req.getStatus() == Status.ACTIVE) {
                        quartzService.createEmailJob(
                                schedule.getId(),
                                schedule.getCronExpression(),
                                schedule.getReceiverEmail(),
                                schedule.getName(),
                                schedule.getEmailTemplate().getBody()
                        );
                    } else if (oldStatus == Status.ACTIVE && req.getStatus() == Status.ACTIVE) {
                        quartzService.updateEmailJob(
                                schedule.getId(),
                                schedule.getCronExpression(),
                                schedule.getReceiverEmail(),
                                schedule.getName(),
                                schedule.getEmailTemplate().getBody()
                        );
                    } else if (oldStatus == Status.ACTIVE && req.getStatus() == Status.INACTIVE) {
                        quartzService.deleteEmailJob(schedule.getId());
                    }
                }
                case "-1" -> {
                    Schedule schedule = scheduleRepo.findById(req.getId())
                            .orElseThrow(() -> new RuntimeException("Schedule không tồn tại"));
                    if (schedule.getStatus().equals(Status.ACTIVE) && req.getStatus().equals(Status.ACTIVE)) {
                        quartzService.deleteEmailJob(req.getId());
                    }
                    scheduleRepo.deleteById(req.getId());

                }
                default -> throw new RuntimeException("Method không hợp lệ");
            }

            return ResponseEntity.ok(ApiRes.success(req, "Thao tác thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiRes.error(e.getMessage(), ApiRes.ErrorCodes.INTERNAL_ERROR));
        }
    }

    @Override
    public ResponseEntity<?> getSchedules(Pageable pageable) {
        Page<Schedule> page = scheduleRepo.findAllByOrderByIdDesc(pageable);
        var result = Map.of(
                "content", page.getContent().stream().map(scheduleMapper::toRes).toList(),
                "pageNumber", page.getNumber(),
                "pageSize", page.getSize(),
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );

        return ResponseEntity.ok(ApiRes.success(result, "Tải dữ liệu thành công"));
    }
}
