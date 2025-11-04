package org.novacore.auth.repository;

import org.novacore.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    long deleteByUser_Id(UUID userId);

    void deleteByExpiresAtBefore(Instant instant);
}
