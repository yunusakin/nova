package org.novacore.lib.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JwtTokenService {

    public static final String ROLES_CLAIM = "roles";

    private final JwtProperties properties;
    private final Key signingKey;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = initSigningKey(properties.secret());
    }

    public JwtTokens issueTokens(String subject, Map<String, Object> additionalClaims, Set<String> roles) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>(additionalClaims == null ? Collections.emptyMap() : additionalClaims);
        claims.put(ROLES_CLAIM, roles == null ? List.of() : List.copyOf(roles));

        Instant accessExpiry = now.plus(properties.accessTokenExpirationMinutes(), ChronoUnit.MINUTES);
        String accessToken = buildToken(subject, claims, accessExpiry, SignatureAlgorithm.HS512);

        Instant refreshExpiry = now.plus(properties.refreshTokenExpirationDays(), ChronoUnit.DAYS);
        String refreshToken = buildToken(subject, Map.of("type", "refresh"), refreshExpiry, SignatureAlgorithm.HS512);

        return new JwtTokens(accessToken, accessExpiry, refreshToken, refreshExpiry);
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(properties.issuer())
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
    }

    public boolean isTokenValid(String token) {
        return validate(() -> parseToken(token)) != null;
    }

    public String extractSubject(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public Set<String> extractRoles(String token) {
        Object claim = parseToken(token).getBody().get(ROLES_CLAIM);
        if (claim instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.toSet());
        }
        return Set.of();
    }

    private <T> T validate(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            return null;
        }
    }

    private String buildToken(String subject, Map<String, Object> claims, Instant expiresAt, SignatureAlgorithm algorithm) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(new HashMap<>(claims))
                .setIssuer(properties.issuer())
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, algorithm)
                .compact();
    }

    private Key initSigningKey(String secret) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ex) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
