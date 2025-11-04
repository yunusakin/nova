package org.novacore.order.service;

import org.novacore.lib.events.ProductCreatedEvent;
import org.novacore.lib.events.UserCreatedEvent;
import org.novacore.order.domain.ProductSummary;
import org.novacore.order.domain.UserSummary;
import org.novacore.order.repository.ProductSummaryRepository;
import org.novacore.order.repository.UserSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ReferenceDataService {

    private final UserSummaryRepository userSummaryRepository;
    private final ProductSummaryRepository productSummaryRepository;

    public ReferenceDataService(UserSummaryRepository userSummaryRepository,
                                ProductSummaryRepository productSummaryRepository) {
        this.userSummaryRepository = userSummaryRepository;
        this.productSummaryRepository = productSummaryRepository;
    }

    public void upsertUser(UserCreatedEvent event) {
        UserSummary summary = userSummaryRepository.findById(event.userId())
                .orElseGet(UserSummary::new);
        summary.setId(event.userId());
        summary.setName(event.name());
        summary.setEmail(event.email());
        userSummaryRepository.save(summary);
    }

    public void upsertProduct(ProductCreatedEvent event) {
        ProductSummary summary = productSummaryRepository.findById(event.productId())
                .orElseGet(ProductSummary::new);
        summary.setId(event.productId());
        summary.setName(event.name());
        summary.setPrice(event.price());
        summary.setStock(event.stock());
        productSummaryRepository.save(summary);
    }

    @Transactional(readOnly = true)
    public boolean userExists(UUID userId) {
        return userSummaryRepository.existsById(userId);
    }

    @Transactional(readOnly = true)
    public boolean productExists(UUID productId) {
        return productSummaryRepository.existsById(productId);
    }
}
