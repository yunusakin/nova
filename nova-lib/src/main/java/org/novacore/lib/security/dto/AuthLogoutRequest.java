package org.novacore.lib.security.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLogoutRequest(@NotBlank String refreshToken) {
}
