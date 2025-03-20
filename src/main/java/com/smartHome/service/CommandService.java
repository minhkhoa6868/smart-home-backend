package com.smartHome.service;

import java.time.LocalDateTime;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import com.smartHome.model.Device;
import com.smartHome.model.CommandType.DoorCommand;
import com.smartHome.model.CommandType.FanCommand;
import com.smartHome.model.CommandType.LightCommand;
import com.smartHome.model.CommandType.LightCommand.Color;
import com.smartHome.repository.DeviceRepository;
import com.smartHome.repository.DoorCommanndRepository;
import com.smartHome.repository.FanCommandRepository;
import com.smartHome.repository.LightCommandRepository;

@Service
public class CommandService {
    private final MqttPublisherService mqttPublisherService;
    private final FanCommandRepository fanCommandRepository;
    private final DoorCommanndRepository doorCommanndRepository;
    private final LightCommandRepository lightCommandRepository;
    private final DeviceRepository deviceRepository;

    public CommandService(MqttPublisherService mqttPublisherService, FanCommandRepository fanCommandRepository, DoorCommanndRepository doorCommanndRepository, LightCommandRepository lightCommandRepository, DeviceRepository deviceRepository) {
        this.mqttPublisherService = mqttPublisherService;
        this.fanCommandRepository = fanCommandRepository;
        this.doorCommanndRepository = doorCommanndRepository;
        this.lightCommandRepository = lightCommandRepository;
        this.deviceRepository = deviceRepository;
    }

    // fan command
    public FanCommand handleCreateFanCommand(Long deviceId, Integer speed) throws MqttException {
        String singal = "ON";
        String type = "FAN";

        Device device = deviceRepository.findByDevice_id(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(deviceId, singal, type);

        // save to database
        FanCommand command = new FanCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setSpeed(speed);

        return fanCommandRepository.save(command);
    }

    // Door command
    public DoorCommand handleCreateDoorCommand(Long deviceId, String status) throws MqttException {
        String singal = "OPEN";
        String type = "DOOR";

        Device device = deviceRepository.findByDevice_id(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(deviceId, singal, type);

        // save to database
        DoorCommand command = new DoorCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setStatus(status);

        return doorCommanndRepository.save(command);
    }

    // light command
    public LightCommand handleCreateLightCommand(Long deviceId, Color color, String status) throws MqttException {
        String singal = "ON";
        String type = "LIGHTCOMMAND";

        Device device = deviceRepository.findByDevice_id(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(deviceId, singal, type);

        // save to database
        LightCommand command = new LightCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setColor(color);
        command.setStatus(status);

        return lightCommandRepository.save(command);
    }
}
