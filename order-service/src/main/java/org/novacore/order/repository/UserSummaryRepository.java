package org.novacore.order.repository;

import jakarta.persistence.LockModeType;
import org.novacore.order.domain.UserSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface UserSummaryRepository extends JpaRepository<UserSummary, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserSummary> findByIdForUpdate(UUID id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<UserSummary> findByIdWithLock(UUID id);
}
