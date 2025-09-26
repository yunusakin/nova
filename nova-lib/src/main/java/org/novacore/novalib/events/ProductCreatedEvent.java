package org.novacore.novalib.events;

import java.io.Serializable;
import java.util.UUID;

public record ProductCreatedEvent(
        UUID productId,
        String name,
        Double price,
        Integer stock
) implements Serializable {}

