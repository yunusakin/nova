package org.novacore.userservice.repository;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.novacore.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(@Email @NotBlank(message = "Mail can not be blank") String email);
}
