package org.novacore.lib.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductCreatedEvent(
        UUID productId,
        String name,
        BigDecimal price,
        int stock,
        Instant occurredAt
) {
}
