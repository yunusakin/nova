package org.novacore.order.repository;

import org.novacore.order.domain.UserSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSummaryRepository extends JpaRepository<UserSummary, UUID> {
}
