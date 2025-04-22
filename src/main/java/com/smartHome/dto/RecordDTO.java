package com.smartHome.dto;

import java.time.ZonedDateTime;

import com.smartHome.model.Record;
import com.smartHome.model.RecordType.Distance;
import com.smartHome.model.RecordType.Humidity;
import com.smartHome.model.RecordType.Light;
import com.smartHome.model.RecordType.Temperature;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordDTO {
    private Long recordId;
    private ZonedDateTime timestamp;
    private Float humidity;
    private Float brightness;
    private Float temperature;
    private Float motion;

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
        if (record instanceof Distance) {
            Distance motion = (Distance) record;
            this.motion = motion.getDistance();
        }
    }
}
