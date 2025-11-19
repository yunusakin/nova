package org.novacore.order.repository;

import jakarta.persistence.LockModeType;
import org.novacore.order.domain.ProductSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface ProductSummaryRepository extends JpaRepository<ProductSummary, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProductSummary> findByIdForUpdate(UUID id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<ProductSummary> findByIdWithLock(UUID id);
}
