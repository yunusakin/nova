package org.novacore.user.security;

import org.novacore.lib.security.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InternalRequestValidator {

    private final String sharedSecret;

    public InternalRequestValidator(@Value("${nova.internal.shared-secret}") String sharedSecret) {
        if (sharedSecret == null || sharedSecret.isBlank()) {
            throw new IllegalArgumentException("Internal shared secret must be configured and non-blank");
        }
        this.sharedSecret = sharedSecret;
    }

    public void validate(String providedSecret) {
        if (providedSecret == null || !java.security.MessageDigest.isEqual(
                sharedSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                providedSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            throw new AuthenticationException("Invalid internal client credentials");
        }
    }
}
