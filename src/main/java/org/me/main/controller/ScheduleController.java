package org.me.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.me.main.dto.req.ScheduleReq;
import org.me.main.dto.req.TemplateReq;
import org.me.main.service.QuartzScheduleService;
import org.me.main.service._interface.IScheduleService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final IScheduleService schedulesService;

    @PostMapping
    private ResponseEntity<?> scheduleOptions(@Valid @RequestBody ScheduleReq req) {
        return schedulesService.scheduleOptions(req);
    }

    @GetMapping
    private ResponseEntity<?> getSchedules(Pageable pageable) {
        return schedulesService.getSchedules(pageable);
    }
}
