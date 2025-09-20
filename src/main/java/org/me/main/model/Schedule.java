package org.me.main.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.me.main.model._enum.Status;
import org.me.main.model._enum.Type;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "Schedules")
public class Schedule extends BaseModel {
    @Column(nullable = false, length = 100)
    String name;
    @Column(length = 100)
    String cronExpression;
    @Column(nullable = false, length = 150)
    String receiverEmail;
    @Enumerated(EnumType.STRING)
    Status status;
    @Enumerated(EnumType.STRING)
    Type type;

    @ManyToOne
    @JoinColumn(name = "template_id")
    EmailTemplate emailTemplate;
}
