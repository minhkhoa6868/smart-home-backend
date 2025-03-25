package com.smartHome.dto;

import com.smartHome.model.CommandType.LightCommand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LightStatusCommandDTO {
    private String status;
    private String deviceId;

    public LightStatusCommandDTO() {}

    public LightStatusCommandDTO(LightCommand command) {
        this.status = command.getStatus();
        this.deviceId = command.getDevice().getDeviceId();
    }
}
