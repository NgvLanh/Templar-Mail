package org.me.main.repo;

import org.me.main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> getUserByEmail(String email);
}
