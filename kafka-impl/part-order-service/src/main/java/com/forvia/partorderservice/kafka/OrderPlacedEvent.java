package com.forvia.partorderservice.kafka;


public record OrderPlacedEvent(
        String orderId,
        String sku,
        int quantity
) {
}
