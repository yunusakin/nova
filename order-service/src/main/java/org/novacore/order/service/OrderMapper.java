package org.novacore.order.service;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.novacore.order.domain.PurchaseOrder;
import org.novacore.order.dto.OrderRequest;
import org.novacore.order.dto.OrderResponse;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PurchaseOrder toEntity(OrderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(OrderRequest request, @MappingTarget PurchaseOrder order);

    OrderResponse toResponse(PurchaseOrder order);

    @AfterMapping
    default void applyTotalPrice(OrderRequest request, @MappingTarget PurchaseOrder order) {
        order.setTotalPrice(request.productPrice().multiply(BigDecimal.valueOf(request.quantity())));
    }
}
