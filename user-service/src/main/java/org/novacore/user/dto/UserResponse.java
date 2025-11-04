package org.novacore.user.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String status,
        Set<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
}
