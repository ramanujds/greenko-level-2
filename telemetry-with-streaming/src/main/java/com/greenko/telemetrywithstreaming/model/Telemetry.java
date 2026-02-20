package com.greenko.telemetrywithstreaming.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "telemetry")
public class Telemetry {

    @Id
    @Field(name = "_id")
    private String id;
    String deviceId;
    double temperature;
    double humidity;
    long timestamp;

}
