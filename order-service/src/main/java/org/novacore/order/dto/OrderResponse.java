package org.novacore.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        UUID productId,
        Integer quantity,
        BigDecimal productPrice,
        BigDecimal totalPrice,
        Instant createdAt,
        Instant updatedAt
) {
}
