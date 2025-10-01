package org.novacore.orderservice.domain.enumeration;

public enum OrderStatus {
    PENDING,     // Order just created, not validated yet
    CONFIRMED,   // User + product validated, order is accepted
    COMPLETED,   // Order fulfilled successfully
    CANCELLED    // Cancelled (user action or validation failure)
}
