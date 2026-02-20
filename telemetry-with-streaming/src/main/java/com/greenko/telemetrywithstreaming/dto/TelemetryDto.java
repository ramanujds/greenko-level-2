package com.greenko.telemetrywithstreaming.dto;

public record TelemetryDto(
        String deviceId,
        double temperature,
        double humidity,
        long timestamp
) {


}
