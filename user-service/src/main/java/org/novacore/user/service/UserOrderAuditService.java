package org.novacore.user.service;

import org.novacore.lib.events.OrderCreatedEvent;
import org.novacore.user.domain.UserOrderAudit;
import org.novacore.user.repository.UserOrderAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserOrderAuditService {

    private final UserOrderAuditRepository auditRepository;

    public UserOrderAuditService(UserOrderAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional
    public void record(OrderCreatedEvent event) {
        UserOrderAudit audit = new UserOrderAudit();
        audit.setOrderId(event.orderId());
        audit.setUserId(event.userId());
        audit.setProductId(event.productId());
        audit.setQuantity(event.quantity());
        audit.setOccurredAt(event.occurredAt());
        auditRepository.save(audit);
    }
}
