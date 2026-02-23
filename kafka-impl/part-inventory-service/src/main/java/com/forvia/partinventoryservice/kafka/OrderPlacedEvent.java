package com.forvia.partinventoryservice.kafka;


public record OrderPlacedEvent(
        String orderId,
        String sku,
        int quantity
) {
}
