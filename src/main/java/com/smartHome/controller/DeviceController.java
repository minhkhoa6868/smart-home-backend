package com.smartHome.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.dto.DeviceDTO;
import com.smartHome.dto.LedDTO;
import com.smartHome.service.CommandService;
import com.smartHome.service.DeviceService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/device")
public class DeviceController {
    private final DeviceService deviceService;
    private final CommandService commandService;

    public DeviceController(DeviceService deviceService, CommandService commandService) {
        this.deviceService = deviceService;
        this.commandService = commandService;
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

    // api for get fan
    @GetMapping("/fan")
    public ResponseEntity<DeviceDTO> getFan() {
        DeviceDTO deviceDTO = deviceService.handleGetDevice("FAN-1");
        return ResponseEntity.ok(deviceDTO);
    }

    // api for get door
    @GetMapping("/door")
    public ResponseEntity<DeviceDTO> getDoor() {
        DeviceDTO deviceDTO = deviceService.handleGetDevice("DOOR-1");
        return ResponseEntity.ok(deviceDTO);
    }

    // api for get led
    @GetMapping("/led")
    public ResponseEntity<LedDTO> getLed() {
        DeviceDTO deviceDTO = deviceService.handleGetDevice("LED-1");
        String color = commandService.getLatestColor();
        return ResponseEntity.ok(new LedDTO(deviceDTO, color));
    }
    
}