package org.novacore.orderservice.controller.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotNull
    private Long userId;

    @NotNull
    private Long productId;

    @Min(1)
    private Integer quantity;

    @Positive
    private Double productPrice;
}
