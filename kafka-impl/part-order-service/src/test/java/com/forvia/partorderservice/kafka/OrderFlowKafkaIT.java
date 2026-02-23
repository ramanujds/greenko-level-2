package com.forvia.partorderservice.kafka;

import com.forvia.partorderservice.dto.OrderRequestDto;
import com.forvia.partorderservice.service.PartOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {OrderTopics.ORDER_PLACED})
class OrderFlowKafkaIT {

    @Autowired
    private PartOrderService partOrderService;

    @DynamicPropertySource
    static void kafkaProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers",
                () -> System.getProperty("spring.embedded.kafka.brokers"));
        registry.add("spring.kafka.admin.auto-create", () -> "false");
    }

    @Test
    void placeOrder_publishesOrderPlacedEvent() {
        partOrderService.placeOrder(new OrderRequestDto("SKU-1", 2));

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test", "true",
                System.getProperty("spring.embedded.kafka.brokers"));
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (var consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps).createConsumer()) {
            consumer.subscribe(java.util.List.of(OrderTopics.ORDER_PLACED));
            var record = KafkaTestUtils.getSingleRecord(consumer, OrderTopics.ORDER_PLACED);
            assertThat(record.key()).isNotBlank();
            assertThat(record.value()).contains("\"sku\":\"SKU-1\"");
            assertThat(record.value()).contains("\"quantity\":2");
        }
    }
}
