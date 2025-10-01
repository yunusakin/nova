package org.novacore.productservice.controller;

import jakarta.validation.Valid;
import org.novacore.novalib.api.ApiResponse;
import org.novacore.productservice.controller.dto.ProductRequestDTO;
import org.novacore.productservice.controller.dto.ProductResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.novacore.productservice.service.ProductService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<ProductResponseDTO>> getAll() {
        return ApiResponse.ok(service.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getById(@PathVariable UUID id) {
        return service.getProductById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> create(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO saved = service.createProduct(dto);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId()))
                .body(ApiResponse.ok(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        try {
            service.deleteProduct(id);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }
}
