package org.novacore.lib.security.dto;

import java.time.Instant;
import java.util.Set;

public record TokenValidationResponse(
        boolean valid,
        String subject,
        Set<String> roles,
        Instant expiresAt
) {
}
