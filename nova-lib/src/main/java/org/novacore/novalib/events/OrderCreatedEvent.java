package org.novacore.novalib.events;

import java.io.Serializable;
import java.util.UUID;
import java.time.Instant;

public record OrderCreatedEvent(
        UUID orderId,
        UUID userId,
        UUID productId,
        Integer quantity,
        Instant createdAt
) implements Serializable {}


