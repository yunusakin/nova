package org.novacore.order.repository;

import org.novacore.order.domain.ProductSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductSummaryRepository extends JpaRepository<ProductSummary, UUID> {
}
