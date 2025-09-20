package org.me.main.dto.res;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemplateRes {
    Long id;
    String name;
    String subject;
    String body;
    Integer numUses;
    Instant createAt;
}
