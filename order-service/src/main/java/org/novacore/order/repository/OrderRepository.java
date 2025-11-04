package org.novacore.order.repository;

import org.novacore.order.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<PurchaseOrder, UUID> {
}
