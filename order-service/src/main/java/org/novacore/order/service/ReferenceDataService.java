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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ReferenceDataService {

    private static final int MAX_UPSERT_ATTEMPTS = 3;

    private final UserSummaryRepository userSummaryRepository;
    private final ProductSummaryRepository productSummaryRepository;

    public ReferenceDataService(UserSummaryRepository userSummaryRepository,
                                ProductSummaryRepository productSummaryRepository) {
        this.userSummaryRepository = userSummaryRepository;
        this.productSummaryRepository = productSummaryRepository;
    }

    public void upsertUser(UserCreatedEvent event) {
        executeWithRetry(() -> {
            UserSummary summary = userSummaryRepository.findByIdForUpdate(event.userId())
                    .orElseGet(() -> new UserSummary(event.userId()));
            summary.setName(event.name());
            summary.setEmail(event.email());
            userSummaryRepository.saveAndFlush(summary);
        });
    }

    public void upsertProduct(ProductCreatedEvent event) {
        executeWithRetry(() -> {
            ProductSummary summary = productSummaryRepository.findByIdForUpdate(event.productId())
                    .orElseGet(() -> new ProductSummary(event.productId()));
            summary.setName(event.name());
            summary.setPrice(event.price());
            summary.setStock(event.stock());
            productSummaryRepository.saveAndFlush(summary);
        });
    }

    @Transactional(readOnly = true)
    public void assertUserExists(UUID userId) {
        userSummaryRepository.findByIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User %s not found".formatted(userId)));
    }

    @Transactional(readOnly = true)
    public void assertProductExists(UUID productId) {
        productSummaryRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product %s not found".formatted(productId)));
    }

    private void executeWithRetry(Runnable action) {
        int attempts = 0;
        while (true) {
            try {
                action.run();
                return;
            } catch (ObjectOptimisticLockingFailureException | DataIntegrityViolationException ex) {
                if (++attempts >= MAX_UPSERT_ATTEMPTS) {
                    throw ex;
                }
                Thread.onSpinWait();
            }
        }
    }
}
