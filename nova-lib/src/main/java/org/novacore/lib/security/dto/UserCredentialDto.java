package org.novacore.lib.security.dto;

import java.util.Set;
import java.util.UUID;

public record UserCredentialDto(
        UUID id,
        String email,
        String password,
        String name,
        String status,
        Set<String> roles
) {
}
