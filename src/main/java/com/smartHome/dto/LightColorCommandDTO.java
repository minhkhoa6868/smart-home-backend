package com.smartHome.dto;

import com.smartHome.model.CommandType.LightCommand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LightColorCommandDTO {
    private String color;
    private String deviceId;
    private Long userId;

    public LightColorCommandDTO() {}

    public LightColorCommandDTO(LightCommand command) {
        this.color = command.getColor();
        this.deviceId = command.getDevice().getDeviceId();
        this.userId = command.getUser().getUserId();
    }
}
