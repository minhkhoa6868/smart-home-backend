package com.smartHome.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.dto.DeviceDTO;
import com.smartHome.service.DeviceService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/device")
public class DeviceController {
    private final com.smartHome.service.DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // api for create device
    @PostMapping
    public ResponseEntity<DeviceDTO> createDevice(@RequestBody DeviceDTO device) {
        DeviceDTO newDevice = deviceService.handleCreateDevice(device);    
        return ResponseEntity.status(HttpStatus.CREATED).body(newDevice);
    }

    // api for get all devices
    @GetMapping("/all")
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        List<DeviceDTO> devices = deviceService.handleGetAllDevices();
        return ResponseEntity.ok(devices);
    }

    // api for get device by id
    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable String deviceId) {
        DeviceDTO deviceDTO = deviceService.handleGetDevice(deviceId);
        return ResponseEntity.ok(deviceDTO);
    }
}