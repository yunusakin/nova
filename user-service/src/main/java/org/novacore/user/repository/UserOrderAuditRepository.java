package org.novacore.user.repository;

import org.novacore.user.domain.UserOrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserOrderAuditRepository extends JpaRepository<UserOrderAudit, UUID> {
}
