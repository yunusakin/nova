package org.novacore.order.service;

import org.novacore.lib.exceptions.ResourceNotFoundException;
import org.novacore.order.domain.PurchaseOrder;
import org.novacore.order.dto.OrderRequest;
import org.novacore.order.dto.OrderResponse;
import org.novacore.order.messaging.OrderEventPublisher;
import org.novacore.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.orderMapper = orderMapper;
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(orderMapper::toResponse).toList();
    }

    public OrderResponse findById(UUID id) {
        return orderMapper.toResponse(getById(id));
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        PurchaseOrder saved = orderRepository.save(orderMapper.toEntity(request));
        eventPublisher.publishOrderCreated(saved.getId(), saved.getUserId(), saved.getProductId(), saved.getQuantity(), saved.getProductPrice(), saved.getTotalPrice());
        return orderMapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse update(UUID id, OrderRequest request) {
        PurchaseOrder order = getById(id);
        orderMapper.updateEntity(request, order);
        return orderMapper.toResponse(order);
    }

    @Transactional
    public void delete(UUID id) {
        PurchaseOrder order = getById(id);
        orderRepository.delete(order);
    }

    private PurchaseOrder getById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order %s not found".formatted(id)));
    }
}
