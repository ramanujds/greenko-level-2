package com.greenko.springbootkafkaconsumer.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaConsumer {

    Logger log = LoggerFactory.getLogger(OrderKafkaConsumer.class);

    @KafkaListener(topics = "orders", groupId = "order-group")
    public void sendNotification(ConsumerRecord<String,String> record){
        log.info("New Message Received {}",record.value());

    }

}
