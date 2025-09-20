package org.me.main.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemplateReq {
    @NotBlank(message = "Method không được để trống")
    @Pattern(regexp = "^-1|0|1$", message = "Method phải là -1, 0 hoặc 1")
    String method;

    @NotBlank(message = "Tên template không được để trống")
    String name;

    @NotBlank(message = "Tiêu đề không được để trống")
    String subject;

    @NotBlank(message = "Nội dung không được để trống")
    String body;

    Long id;
}
