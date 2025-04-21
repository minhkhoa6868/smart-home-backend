package com.smartHome.dto;

import com.smartHome.model.CommandType.DoorCommand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoorCommandDTO {
    private String status;
    private String deviceId;
    private Long userId;

    public DoorCommandDTO() {}

    public DoorCommandDTO(DoorCommand command) {
        this.status = command.getStatus();
        this.deviceId = command.getDevice().getDeviceId();
        this.userId = command.getUser().getUserId();
    }
}
