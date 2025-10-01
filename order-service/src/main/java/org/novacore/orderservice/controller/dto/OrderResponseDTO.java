package org.novacore.orderservice.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.novacore.orderservice.domain.enumeration.OrderStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private UUID id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Double totalAmount;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
