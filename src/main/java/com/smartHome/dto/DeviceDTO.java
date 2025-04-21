package com.smartHome.dto;

import com.smartHome.model.Device;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceDTO {
    private String deviceId;
    private String deviceName;
    private String status;
    private Long power;

    // No-args constructor (needed by Jackson)
    public DeviceDTO() {}

    public DeviceDTO(Device device) {
        this.deviceId = device.getDeviceId();
        this.deviceName = device.getDevice_name();
        this.status = device.getStatus();
        this.power = device.getPower();
    }
}
