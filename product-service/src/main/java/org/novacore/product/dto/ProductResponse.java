package org.novacore.product.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        BigDecimal price,
        Integer stock,
        Instant createdAt,
        Instant updatedAt
) {
}
