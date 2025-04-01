package com.smartHome.dto;

import com.smartHome.model.CommandType.FanCommand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FanCommandDTO {
    private String speed;
    private String deviceId;

    public FanCommandDTO() {}

    public FanCommandDTO(FanCommand command) {
        this.speed = command.getSpeed();
        this.deviceId = command.getDevice().getDeviceId();
    }
}
