package org.novacore.orderservice.service;

import org.novacore.orderservice.domain.Order;
import org.novacore.orderservice.controller.dto.OrderRequestDTO;
import org.novacore.orderservice.controller.dto.OrderResponseDTO;
import org.novacore.orderservice.domain.enumeration.OrderStatus;
import org.novacore.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    private OrderResponseDTO toResponse(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private Order toEntity(OrderRequestDTO dto, double totalAmount) {
        return Order.builder()
                .userId(dto.getUserId())
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();
    }

    // --- Business methods ---
    public List<OrderResponseDTO> getAllOrders() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<OrderResponseDTO> getOrderById(UUID id) {
        return repo.findById(id).map(this::toResponse);
    }

    public OrderResponseDTO createOrder(OrderRequestDTO dto, double productPrice) {
        double totalAmount = dto.getQuantity() * productPrice;
        if (totalAmount <= 0) {
            throw new IllegalArgumentException("Total amount must be positive");
        }

        Order order = toEntity(dto, totalAmount);
        order.setStatus(OrderStatus.CONFIRMED);

        Order saved = repo.save(order);
        return toResponse(saved);
    }

    public void deleteOrder(UUID id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Order not found: " + id);
        }
        repo.deleteById(id);
    }
}