package org.novacore.productservice.service;
import org.novacore.productservice.controller.dto.ProductRequestDTO;
import org.novacore.productservice.controller.dto.ProductResponseDTO;
import org.novacore.productservice.domain.Product;
import org.springframework.stereotype.Service;
import org.novacore.productservice.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    private ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }

    private Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
    }

    public List<ProductResponseDTO> getAllProducts() {
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Optional<ProductResponseDTO> getProductById(UUID id) {
        return repo.findById(id).map(this::toResponse);
    }

    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        if (dto.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        Product saved = repo.save(toEntity(dto));
        return toResponse(saved);
    }

    public void deleteProduct(UUID id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        repo.deleteById(id);
    }
}

