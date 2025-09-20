package org.me.main.mapper;

import org.mapstruct.Mapper;
import org.me.main.dto.res.ScheduleRes;
import org.me.main.model.Schedule;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    ScheduleRes toRes(Schedule schedule);
}
