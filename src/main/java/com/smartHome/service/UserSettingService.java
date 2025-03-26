package com.smartHome.service;

import org.springframework.stereotype.Service;

import com.smartHome.model.Device;
import com.smartHome.model.UserSetting;
import com.smartHome.repository.DeviceRepository;
import com.smartHome.repository.UserSettingRepository;

@Service
public class UserSettingService {
    private final UserSettingRepository userSettingRepository;
    private final DeviceRepository deviceRepository;

    public UserSettingService(UserSettingRepository userSettingRepository, DeviceRepository deviceRepository) {
        this.userSettingRepository = userSettingRepository;
        this.deviceRepository = deviceRepository;
    }

    // handle turn on auto mode
    public void handleAutoMode(String deviceId, Boolean autoMode, Float desireTemperature) {
        Device device = deviceRepository.findByDeviceId(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        UserSetting setting = userSettingRepository.findByDevice(device)
            .orElse(new UserSetting());

        setting.setDevice(device);
        setting.setAutoMode(autoMode);
        setting.setDesireTemperature(desireTemperature);
        userSettingRepository.save(setting);
    }
}
