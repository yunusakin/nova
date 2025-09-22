package org.novacore.orderservice.service;
import org.novacore.orderservice.controller.dto.OrderRequestDTO;
import org.novacore.orderservice.controller.dto.OrderResponseDTO;
import org.novacore.orderservice.domain.Order;
import org.springframework.stereotype.Service;
import org.novacore.orderservice.repository.OrderRepository;

import java.util.List;
import java.util.Optional;
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
                .customer(order.getCustomer())
                .totalAmount(order.getTotalAmount())
                .build();
    }

    private Order toEntity(OrderRequestDTO dto) {
        return Order.builder()
                .customer(dto.getCustomer())
                .totalAmount(dto.getTotalAmount())
                .build();
    }

    public List<OrderResponseDTO> getAllOrders() {
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Optional<OrderResponseDTO> getOrderById(Long id) {
        return repo.findById(id).map(this::toResponse);
    }

    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        if (dto.getTotalAmount() <= 0) {
            throw new IllegalArgumentException("Total amount must be positive");
        }
        Order saved = repo.save(toEntity(dto));
        return toResponse(saved);
    }

    public void deleteOrder(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Order not found: " + id);
        }
        repo.deleteById(id);
    }
}

