package org.novacore.user.security;

import org.novacore.lib.security.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InternalRequestValidator {

    private final String sharedSecret;

    public InternalRequestValidator(@Value("${nova.internal.shared-secret}") String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public void validate(String providedSecret) {
        if (sharedSecret == null || sharedSecret.isBlank()) {
            throw new AuthenticationException("Internal secret is not configured");
        }
        if (providedSecret == null || !sharedSecret.equals(providedSecret)) {
            throw new AuthenticationException("Invalid internal client credentials");
        }
    }
}
