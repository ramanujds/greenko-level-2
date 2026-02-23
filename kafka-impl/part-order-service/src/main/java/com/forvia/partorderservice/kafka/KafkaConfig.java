package com.forvia.partorderservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderPlacedTopic(@Value("${app.kafka.topics.order-placed:" + OrderTopics.ORDER_PLACED + "}") String name,
                                     @Value("${app.kafka.topics.partitions:1}") int partitions,
                                     @Value("${app.kafka.topics.replication-factor:1}") short replicationFactor) {
        return TopicBuilder.name(name).partitions(partitions).replicas(replicationFactor).build();
    }

    @Bean
    public RecordMessageConverter recordMessageConverter() {
        return new StringJsonMessageConverter();
    }
}
