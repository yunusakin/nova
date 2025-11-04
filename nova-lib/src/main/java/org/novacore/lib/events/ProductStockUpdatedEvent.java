package org.novacore.lib.events;

import java.time.Instant;
import java.util.UUID;

public record ProductStockUpdatedEvent(
        UUID productId,
        int stock,
        Instant occurredAt
) {
}
