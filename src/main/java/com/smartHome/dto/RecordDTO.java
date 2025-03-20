package com.smartHome.dto;

import java.time.LocalDateTime;

import com.smartHome.model.Record;
import com.smartHome.model.RecordType.Humidity;
import com.smartHome.model.RecordType.Light;
import com.smartHome.model.RecordType.Motion;
import com.smartHome.model.RecordType.Temperature;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordDTO {
    private Long recordId;
    private LocalDateTime timestamp;
    private Double humidity;
    private Double brightness;
    private Double temperature;
    private Boolean motion;

    public RecordDTO() {}

    public RecordDTO(Record record) {
        this.recordId = record.getRecordId();
        this.timestamp = record.getTimestamp();
        if (record instanceof Humidity) {
            Humidity humidity = (Humidity) record;
            this.humidity = humidity.getTemperature();
        }
        if (record instanceof Light) {
            Light light = (Light) record;
            this.brightness = light.getBrightness();
        }
        if (record instanceof Temperature) {
            Temperature temperature = (Temperature) record;
            this.temperature = temperature.getTemperature();
        }
        if (record instanceof Motion) {
            Motion motion = (Motion) record;
            this.motion = motion.getMotion();
        }
    }
}
