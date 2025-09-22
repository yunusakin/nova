package org.novacore.orderservice.controller;

import org.novacore.novalib.api.ApiResponse;
import org.novacore.orderservice.controller.dto.OrderRequestDTO;
import org.novacore.orderservice.controller.dto.OrderResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.novacore.orderservice.service.OrderService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/org/novacore/novalib/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<OrderResponseDTO>> getAll() {
        return ApiResponse.ok(service.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getById(@PathVariable Long id) {
        return service.getOrderById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> create(@RequestBody OrderRequestDTO dto) {
        OrderResponseDTO saved = service.createOrder(dto);
        return ResponseEntity.created(URI.create("/api/orders/" + saved.getId()))
                .body(ApiResponse.ok(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            service.deleteOrder(id);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }
}
