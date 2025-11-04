package org.novacore.lib.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID userId,
        UUID productId,
        int quantity,
        BigDecimal productPrice,
        BigDecimal totalPrice,
        Instant occurredAt
) {
}
