package org.novacore.product.service;

import org.novacore.lib.exceptions.ResourceNotFoundException;
import org.novacore.product.domain.Product;
import org.novacore.product.dto.ProductRequest;
import org.novacore.product.dto.ProductResponse;
import org.novacore.product.messaging.ProductEventPublisher;
import org.novacore.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductEventPublisher eventPublisher, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
        this.productMapper = productMapper;
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(productMapper::toResponse).toList();
    }

    public ProductResponse findById(UUID id) {
        return productMapper.toResponse(getById(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product saved = productRepository.save(productMapper.toEntity(request));
        eventPublisher.publishProductCreated(saved.getId(), saved.getName(), saved.getPrice(), saved.getStock());
        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = getById(id);
        productMapper.updateEntity(request, product);
        return productMapper.toResponse(product);
    }

    @Transactional
    public void delete(UUID id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    private Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product %s not found".formatted(id)));
    }
}
