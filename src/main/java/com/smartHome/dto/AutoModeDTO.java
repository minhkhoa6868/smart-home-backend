package com.smartHome.dto;

import java.time.ZonedDateTime;

import com.smartHome.model.Device;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoModeDTO {
    private Boolean isAutoMode;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    public AutoModeDTO() {}

    public AutoModeDTO(Device device) {
        this.isAutoMode = device.getIsAutoMode();
        this.startTime = device.getAlertStartTime();
        this.endTime = device.getAlertEndTime();
    }
}
