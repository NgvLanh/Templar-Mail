package org.me.main.service._interface;

import org.me.main.dto.req.ScheduleReq;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IScheduleService {
    ResponseEntity<?> scheduleOptions(ScheduleReq req);
    ResponseEntity<?> getSchedules(Pageable pageable);

}
