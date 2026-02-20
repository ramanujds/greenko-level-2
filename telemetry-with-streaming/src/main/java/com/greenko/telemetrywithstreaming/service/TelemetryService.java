package com.greenko.telemetrywithstreaming.service;

import com.greenko.telemetrywithstreaming.dto.TelemetryDto;
import com.greenko.telemetrywithstreaming.repository.TelemetryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;
    List<TelemetryDto> telemetryList;

    public TelemetryService(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }


//    @PostConstruct
//    void init(){
//        telemetryList = new ArrayList<>();
//        telemetryList.add(new Telemetry("1", 20.0, 30.0, 1647366400000L));
//        telemetryList.add(new Telemetry("2", 21.0, 31.0, 1647366460000L));
//        telemetryList.add(new Telemetry("3", 22.0, 32.0, 1647366520000L));
//        telemetryList.add(new Telemetry("4", 23.0, 33.0, 1647366580000L));
//        telemetryList.add(new Telemetry("5", 24.0, 34.0, 1647366640000L));
//        telemetryList.add(new Telemetry("6", 25.0, 35.0, 1647366700000L));
//        telemetryList.add(new Telemetry("7", 26.0, 36.0, 1647366760000L));
//        telemetryList.add(new Telemetry("8", 27.0, 37.0, 1647366820000L));
//        telemetryList.add(new Telemetry("9", 28.0, 38.0, 1647366880000L));
//        telemetryList.add(new Telemetry("10", 29.0, 39.0, 1647366940000L));
//        telemetryList.add(new Telemetry("11", 30.0, 40.0, 1647367000000L));
//        telemetryList.add(new Telemetry("12", 31.0, 41.0, 1647367060000L));
//        telemetryList.add(new Telemetry("13", 32.0, 42.0, 1647367120000L));
//        telemetryList.add(new Telemetry("14", 33.0, 43.0, 1647367180000L));
//        telemetryList.add(new Telemetry("15", 34.0, 44.0, 1647367240000L));
//    }

    public void sendTelemetry(TelemetryDto telemetry) {

    }

    public Flux<TelemetryDto> fetchTelemetry() {
        return telemetryRepository.findAll()
              //  .filter(telemetry -> telemetry.getTemperature()>24)
                .map(telemetry -> new TelemetryDto(
                        telemetry.getDeviceId(),
                        telemetry.getTemperature(),
                        telemetry.getHumidity(),
                        telemetry.getTimestamp()
                )).delayElements(Duration.ofSeconds(1))

                .doOnNext(telemetry -> log.info("Telemetry: {}", telemetry))

                ;
    }

}
