package com.forvia.partorderservice.service;

import com.forvia.partorderservice.client.InventoryServiceClient;
import com.forvia.partorderservice.dto.OrderRequestDto;
import com.forvia.partorderservice.dto.OrderResponseDto;
import com.forvia.partorderservice.dto.PartDto;
import com.forvia.partorderservice.kafka.OrderPlacedEvent;
import com.forvia.partorderservice.kafka.OrderRequestKafkaProducer;
import com.forvia.partorderservice.kafka.OrderTopics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PartOrderService {

    private final InventoryServiceClient inventoryServiceClient;
    private final OrderRequestKafkaProducer producer;
    private final String orderPlacedTopic;

    public PartOrderService(InventoryServiceClient inventoryServiceClient,
                            OrderRequestKafkaProducer producer,
                            @Value("${app.kafka.topics.order-placed:" + OrderTopics.ORDER_PLACED + "}") String orderPlacedTopic) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.producer = producer;
        this.orderPlacedTopic = orderPlacedTopic;
    }

    public List<PartDto> getAllAvailableParts() {
        return inventoryServiceClient.getAllParts();
    }

    /**
     * Places the order locally and emits an event for inventory to update stock asynchronously.
     *
     * Note: This simplified flow does not confirm stock availability synchronously.
     */
    public OrderResponseDto placeOrder(OrderRequestDto orderRequest) {
        String orderId = UUID.randomUUID().toString();
        producer.send(new OrderPlacedEvent(orderId, orderRequest.sku(), orderRequest.quantity()), orderPlacedTopic);

        return new OrderResponseDto(orderRequest.sku(), "Order placed (inventory update pending)",
                orderRequest.quantity() == null ? 0 : orderRequest.quantity(), 0);
    }
}