package org.novacore.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderRequest(
        @NotNull(message = "User ID is required")
        UUID userId,
        @NotNull(message = "Product ID is required")
        UUID productId,
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,
        @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be positive")
        BigDecimal productPrice
) {
}
