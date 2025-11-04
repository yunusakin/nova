package org.novacore.lib.events;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID orderId,
        UUID userId,
        UUID productId,
        Instant occurredAt,
        String reason
) {
}
