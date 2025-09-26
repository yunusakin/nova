package org.novacore.novalib.events;


import java.io.Serializable;
import java.util.UUID;

public record UserCreatedEvent(
        UUID userId,
        String name,
        String email
) implements Serializable {}
