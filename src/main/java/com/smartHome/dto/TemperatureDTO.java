package com.smartHome.dto;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemperatureDTO {
    private ZonedDateTime timestamp;
    private Float temperature;

    public TemperatureDTO() {}

    public TemperatureDTO(ZonedDateTime timestamp, Float temperature) {
        this.timestamp = timestamp;
        this.temperature = temperature;
    }
}
