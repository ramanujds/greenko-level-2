package com.greenko.springbootkafkaproducer.kafka;

import com.greenko.springbootkafkaproducer.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrdersKafkaProducer {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessageInBulk() {
        for(int i=0;i<1000;i++){
            sendMessage(new OrderDto("order-"+ i%50
                    ,"Hello World"));
        }
    }

    public void sendMessage(OrderDto order) {
        kafkaTemplate.send("orders", order.orderId()+":"+order.message());
    }

}
