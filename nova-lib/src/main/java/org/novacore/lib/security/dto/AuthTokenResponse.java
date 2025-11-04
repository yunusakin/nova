package org.novacore.lib.security.dto;

import java.time.Instant;
import java.util.Set;

public record AuthTokenResponse(
        String tokenType,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        Set<String> roles
) {
}
