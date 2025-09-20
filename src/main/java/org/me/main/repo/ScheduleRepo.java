package org.me.main.repo;

import org.me.main.model.Schedule;
import org.me.main.model._enum.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    Page<Schedule> findAllByOrderByIdDesc(Pageable pageable);

    List<Schedule> findByStatus(Status status);
}
