package org.me.main.dto.res;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.me.main.model.EmailTemplate;
import org.me.main.model.User;
import org.me.main.model._enum.Status;
import org.me.main.model._enum.Type;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleRes {
    String name;
    Type type;
    String cronExpression;
    String receiverEmail;
    Status status;
    TemplateRes emailTemplate;

    Long id;


    public String getType() {
        return type.getDisplay();
    }

    public String getStatus() {
        return status.getDisplay();
    }
}
