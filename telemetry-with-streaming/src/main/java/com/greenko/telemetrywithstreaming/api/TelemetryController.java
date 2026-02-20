package com.greenko.telemetrywithstreaming.api;

import com.greenko.telemetrywithstreaming.dto.TelemetryDto;
import com.greenko.telemetrywithstreaming.service.TelemetryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {


    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping(value = "/fetch", produces = "text/event-stream")
    public Flux<TelemetryDto> fetchTelemetry(){
        return telemetryService.fetchTelemetry();
    }



}
