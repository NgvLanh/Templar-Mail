package org.me.main.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.me.main.model._enum.Status;
import org.me.main.model._enum.Type;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleReq {

    @NotBlank(message = "Method không được để trống")
    @Pattern(regexp = "^-1|0|1$", message = "Method phải là -1, 0 hoặc 1")
    String method;

    @NotBlank(message = "Tên template không được để trống")
    String name;

    @NotNull(message = "Loại lịch (type) không được để trống")
    Type type;

    @NotBlank(message = "Biểu thức CRON không được để trống")
    String cronExpression;

    String receiverEmail;

    Status status;

    Long templateId;

    Long id;


}
