package org.me.main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;
import org.me.main.model._enum.Status;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "users")
public class User extends BaseModel {
    @Column(nullable = false, length = 100)
    String name;
    @Column(nullable = false, length = 150)
    String email;
    @Enumerated(EnumType.STRING)
    Status status;
    @UpdateTimestamp
    Instant updateAt;
}
