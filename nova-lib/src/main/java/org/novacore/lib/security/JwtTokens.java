package org.novacore.lib.security;

import java.time.Instant;
import java.util.Objects;

public record JwtTokens(
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
    public JwtTokens {
        Objects.requireNonNull(accessToken, "accessToken");
        Objects.requireNonNull(accessTokenExpiresAt, "accessTokenExpiresAt");
        Objects.requireNonNull(refreshToken, "refreshToken");
        Objects.requireNonNull(refreshTokenExpiresAt, "refreshTokenExpiresAt");
    }
}
