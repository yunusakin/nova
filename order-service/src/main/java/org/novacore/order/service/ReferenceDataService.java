package org.novacore.order.service;

import org.novacore.lib.events.ProductCreatedEvent;
import org.novacore.lib.events.UserCreatedEvent;
import org.novacore.lib.exceptions.ResourceNotFoundException;
import org.novacore.order.domain.ProductSummary;
import org.novacore.order.domain.UserSummary;
import org.novacore.order.repository.ProductSummaryRepository;
import org.novacore.order.repository.UserSummaryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReferenceDataService {

    private static final int MAX_UPSERT_ATTEMPTS = 3;

    private final UserSummaryRepository userSummaryRepository;
    private final ProductSummaryRepository productSummaryRepository;

    public ReferenceDataService(UserSummaryRepository userSummaryRepository,
                                ProductSummaryRepository productSummaryRepository) {
        this.userSummaryRepository = userSummaryRepository;
        this.productSummaryRepository = productSummaryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class, DataIntegrityViolationException.class},
            maxAttempts = MAX_UPSERT_ATTEMPTS,
            backoff = @Backoff(delay = 100, multiplier = 2.0)
    )
    public void upsertUser(UserCreatedEvent event) {
        UserSummary summary = userSummaryRepository.findByIdForUpdate(event.userId())
                .orElseGet(() -> new UserSummary(event.userId()));
        summary.setName(event.name());
        summary.setEmail(event.email());
        userSummaryRepository.saveAndFlush(summary);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class, DataIntegrityViolationException.class},
            maxAttempts = MAX_UPSERT_ATTEMPTS,
            backoff = @Backoff(delay = 100, multiplier = 2.0)
    )
    public void upsertProduct(ProductCreatedEvent event) {
        ProductSummary summary = productSummaryRepository.findByIdForUpdate(event.productId())
                .orElseGet(() -> new ProductSummary(event.productId()));
        summary.setName(event.name());
        summary.setPrice(event.price());
        summary.setStock(event.stock());
        productSummaryRepository.saveAndFlush(summary);
    }

    public void assertUserExists(UUID userId) {
        if (!userSummaryRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User %s not found".formatted(userId));
        }
    }

    public void assertProductExists(UUID productId) {
        if (!productSummaryRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product %s not found".formatted(productId));
        }
    }


}
