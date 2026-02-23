package com.forvia.partorderservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderRequestKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderRequestKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(OrderPlacedEvent event, String topic) {
        kafkaTemplate.send(topic, event.orderId(), event);
    }
}
