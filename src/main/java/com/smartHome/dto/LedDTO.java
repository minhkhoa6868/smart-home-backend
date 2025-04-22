package com.smartHome.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LedDTO {
    private String deviceId;
    private String deviceName;
    private String status;
    private Long power;
    private String color;

    public LedDTO() {}

    public LedDTO(DeviceDTO deviceDTO, String color) {
        this.deviceId = deviceDTO.getDeviceId();
        this.deviceName = deviceDTO.getDeviceName();
        this.status = deviceDTO.getStatus();
        this.power = deviceDTO.getPower();
        this.color = color;
    }
}
