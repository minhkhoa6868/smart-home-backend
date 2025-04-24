package com.smartHome.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.smartHome.dto.AutoModeDTO;
import com.smartHome.dto.DeviceDTO;
import com.smartHome.model.Device;
import com.smartHome.repository.DeviceRepository;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    // handle create device
    public DeviceDTO handleCreateDevice(DeviceDTO device) {
        // check if device already exists
        if (deviceRepository.findByDeviceId(device.getDeviceId()).isPresent()) {
            throw new RuntimeException("Device already exists!");
        }

        // save to database
        Device newDevice = new Device();
        newDevice.setDeviceId(device.getDeviceId());
        newDevice.setDevice_name(device.getDeviceName());
        newDevice.setStatus(device.getStatus());
        newDevice.setPower(device.getPower());
        newDevice.setIsAutoMode(false);
        deviceRepository.save(newDevice);

        return new DeviceDTO(newDevice);
    }

    // handle get all device
    public List<DeviceDTO> handleGetAllDevices() {
        List<Device> devices = deviceRepository.findAll();

        return devices.stream()
                .sorted(Comparator.comparing(Device::getDeviceId))
                .map(DeviceDTO::new)
                .collect(Collectors.toList());
    }

    // handle update device
    public DeviceDTO handleUpdateDevice(DeviceDTO device) {
        // check if device exists
        Device existingDevice = deviceRepository.findByDeviceId(device.getDeviceId())
              .orElseThrow(() -> new RuntimeException("Device not found!"));

        // update device
        existingDevice.setDevice_name(device.getDeviceName());
        existingDevice.setStatus(device.getStatus());
        existingDevice.setPower(device.getPower());
        deviceRepository.save(existingDevice);

        return new DeviceDTO(existingDevice);
    }

    // handle update device status
    public void handleUpdateDeviceStatus(String deviceId, String status) {
        System.out.println(deviceId + status);
        // check if device exists
        Device existingDevice = deviceRepository.findByDeviceId(deviceId)
              .orElseThrow(() -> new RuntimeException("Device not found!"));

        // update device status
        existingDevice.setStatus(status);
        deviceRepository.save(existingDevice);
    }

    // handle get device
    public DeviceDTO handleGetDevice(String deviceId) {
        // check if device exists
        Device existingDevice = deviceRepository.findByDeviceId(deviceId)
              .orElseThrow(() -> new RuntimeException("Device not found!"));

        return new DeviceDTO(existingDevice);
    }

    // handle get auto mode
    public AutoModeDTO handleGetAutoModeDevice(String deviceId) {
        // check if device exists
        Device existingDevice = deviceRepository.findByDeviceId(deviceId)
              .orElseThrow(() -> new RuntimeException("Device not found!"));

        return new AutoModeDTO(existingDevice.getIsAutoMode());
    }
}
