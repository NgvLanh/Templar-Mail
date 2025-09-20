package org.me.main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "email_templates")
public class EmailTemplate extends BaseModel {
    @Column(nullable = false, length = 100)
    String name;
    @Column(nullable = false, length = 200)
    String subject;
    @Column(columnDefinition = "text")
    String body;

    @OneToMany(mappedBy = "emailTemplate")
    List<Schedule> schedules;
}
