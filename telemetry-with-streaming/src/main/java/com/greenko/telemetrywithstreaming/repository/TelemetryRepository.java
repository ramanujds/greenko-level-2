package com.greenko.telemetrywithstreaming.repository;

import com.greenko.telemetrywithstreaming.dto.TelemetryDto;
import com.greenko.telemetrywithstreaming.model.Telemetry;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TelemetryRepository extends ReactiveMongoRepository<Telemetry, String> {

    Flux<TelemetryDto> findByDeviceId(String deviceId);

}
