package com.smartHome.service;

import java.time.LocalDateTime;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import com.smartHome.dto.FanCommandDTO;
import com.smartHome.dto.LightColorCommandDTO;
import com.smartHome.dto.LightStatusCommandDTO;
import com.smartHome.model.Device;
import com.smartHome.model.CommandType.DoorCommand;
import com.smartHome.model.CommandType.FanCommand;
import com.smartHome.model.CommandType.LightCommand;
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
    private final DeviceService deviceService;

    public CommandService(MqttPublisherService mqttPublisherService, FanCommandRepository fanCommandRepository, DoorCommanndRepository doorCommanndRepository, LightCommandRepository lightCommandRepository, DeviceRepository deviceRepository, DeviceService deviceService) {
        this.mqttPublisherService = mqttPublisherService;
        this.fanCommandRepository = fanCommandRepository;
        this.doorCommanndRepository = doorCommanndRepository;
        this.lightCommandRepository = lightCommandRepository;
        this.deviceRepository = deviceRepository;
        this.deviceService = deviceService;
    }

    // fan command
    public FanCommandDTO handleCreateFanCommand(Integer speed, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("FAN-1")
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, speed.toString());

        // save to database
        FanCommand command = new FanCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setSpeed(speed);
        fanCommandRepository.save(command);

        if (speed == 0) {
            deviceService.handleUpdateDeviceStatus("FAN-1", "OFF");
        }

        else {
            deviceService.handleUpdateDeviceStatus("FAN-1", "ON");
        }

        return new FanCommandDTO(command);
    }

    // Door command
    public DoorCommand handleCreateDoorCommand(String deviceId, String status, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, status);

        // save to database
        DoorCommand command = new DoorCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setStatus(status);

        return doorCommanndRepository.save(command);
    }

    // light color command
    public LightColorCommandDTO handleCreateLightColorCommand(Integer color, String topic) throws MqttException {     
        Device device = deviceRepository.findByDeviceId("LED-1")
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        if ("OFF".equals(device.getStatus())){
            throw new RuntimeException("Light is off!");
        }

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, color.toString());

        // save to database
        LightCommand command = new LightCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setStatus("ON");
        command.setColor(color);
        lightCommandRepository.save(command);

        return new LightColorCommandDTO(command);
    }

    //  light status command
    public LightStatusCommandDTO handleCreateLightStatusCommand(String status, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("LED-1")
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, status);

        // save to database
        LightCommand command = new LightCommand();

        // if status is on in the first time, it will set default color is one
        // else it still keep the previous color
        if ("ON".equals(status)) {
            if ("ON".equals(device.getStatus())) {
                throw new RuntimeException("Light is already on!");
            }

            Integer color = getLatestColor(device);

            if (color == null) {
                command.setColor(1);
                mqttPublisherService.publishCommand(topic, "1");
            }

            else {
                command.setColor(color);
                mqttPublisherService.publishCommand(topic, color.toString());
            }
        }

        else if ("OFF".equals(status)) {
            if ("OFF".equals(device.getStatus())) {
                throw new RuntimeException("Light is already off!");
            }
            Integer color = getLatestColor(device);
            command.setColor(color);
            mqttPublisherService.publishCommand(topic, "0");
        }

        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setStatus(status);

        deviceService.handleUpdateDeviceStatus("LED-1", status);

        lightCommandRepository.save(command);

        return new LightStatusCommandDTO(command);
    }

    // get latest color
    public Integer getLatestColor(Device device) {
        return lightCommandRepository
                .findTopByDeviceOrderByTimestampDesc(device)
                .map(LightCommand::getColor)
                .orElse(null); // or throw exception if preferred
    }
}