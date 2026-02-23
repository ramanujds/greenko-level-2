package com.greenko.springbootkafkaproducer.api;

import com.greenko.springbootkafkaproducer.dto.OrderDto;
import com.greenko.springbootkafkaproducer.kafka.OrdersKafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrdersController {


    @Autowired
    OrdersKafkaProducer producer;

    @PostMapping
    public void createOrder(@RequestBody OrderDto order) {
        new Thread(() -> producer.sendMessageInBulk()).start();
        new Thread(() -> producer.sendMessageInBulk()).start();
        new Thread(() -> producer.sendMessageInBulk()).start();
        new Thread(() -> producer.sendMessageInBulk()).start();
    }


}
