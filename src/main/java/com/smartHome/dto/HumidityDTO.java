package com.smartHome.dto;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HumidityDTO {
    private ZonedDateTime timestamp;
    private Float humidity;

    public HumidityDTO() {}

    public HumidityDTO(ZonedDateTime timestamp, Float humidity) {
        this.timestamp = timestamp;
        this.humidity = humidity;
    }
}
