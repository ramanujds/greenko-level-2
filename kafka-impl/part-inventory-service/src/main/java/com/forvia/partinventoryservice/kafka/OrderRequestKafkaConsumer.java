package com.forvia.partinventoryservice.kafka;

import com.forvia.partinventoryservice.model.Part;
import com.forvia.partinventoryservice.repository.PartRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderRequestKafkaConsumer {

    private final PartRepository partRepository;

    public OrderRequestKafkaConsumer(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-placed:" + OrderTopics.ORDER_PLACED + "}",
            groupId = "${spring.kafka.consumer.group-id:part-inventory-service}")
    public void onOrderPlaced(OrderPlacedEvent event) {
        List<Part> parts = partRepository.findBySku(event.sku());
        if (parts.isEmpty()) {
            return;
        }

        Part part = parts.get(0);
        int newStock = part.getStock() - event.quantity();
        part.setStock(Math.max(newStock, 0));
        partRepository.save(part);
    }
}
