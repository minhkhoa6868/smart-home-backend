package com.smartHome.service;

import java.time.LocalDateTime;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.smartHome.dto.DoorCommandDTO;
import com.smartHome.dto.FanCommandDTO;
import com.smartHome.dto.LightColorCommandDTO;
import com.smartHome.dto.LightStatusCommandDTO;
import com.smartHome.model.Device;
import com.smartHome.model.UserSetting;
import com.smartHome.model.CommandType.DoorCommand;
import com.smartHome.model.CommandType.FanCommand;
import com.smartHome.model.CommandType.LightCommand;
import com.smartHome.model.RecordType.Light;
import com.smartHome.repository.BrightnessRepository;
import com.smartHome.repository.DeviceRepository;
import com.smartHome.repository.DoorCommanndRepository;
import com.smartHome.repository.FanCommandRepository;
import com.smartHome.repository.LightCommandRepository;
import com.smartHome.repository.UserSettingRepository;

@Service
public class CommandService {
    private final MqttPublisherService mqttPublisherService;
    private final FanCommandRepository fanCommandRepository;
    private final DoorCommanndRepository doorCommanndRepository;
    private final LightCommandRepository lightCommandRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceService deviceService;
    private final BrightnessRepository brightnessRepository;
    private final UserSettingRepository userSettingRepository;

    public CommandService(MqttPublisherService mqttPublisherService, FanCommandRepository fanCommandRepository,
            DoorCommanndRepository doorCommanndRepository, LightCommandRepository lightCommandRepository,
            DeviceRepository deviceRepository, DeviceService deviceService, BrightnessRepository brightnessRepository,
            UserSettingRepository userSettingRepository) {
        this.mqttPublisherService = mqttPublisherService;
        this.fanCommandRepository = fanCommandRepository;
        this.doorCommanndRepository = doorCommanndRepository;
        this.lightCommandRepository = lightCommandRepository;
        this.deviceRepository = deviceRepository;
        this.deviceService = deviceService;
        this.brightnessRepository = brightnessRepository;
        this.userSettingRepository = userSettingRepository;
    }

    // fan command
    public FanCommandDTO handleCreateFanCommand(String speed, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("FAN-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, speed);

        // save to database
        FanCommand command = new FanCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setSpeed(speed);
        fanCommandRepository.save(command);

        if ("0".equals(speed)) {
            deviceService.handleUpdateDeviceStatus("FAN-1", "OFF");
        }

        else if ("1".equals(speed) || "2".equals(speed) || "3".equals(speed)) {
            deviceService.handleUpdateDeviceStatus("FAN-1", "ON");
        }

        return new FanCommandDTO(command);
    }

    // Door command
    public DoorCommandDTO handleCreateDoorCommand(String status, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("DOOR-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, status);

        // save to database
        DoorCommand command = new DoorCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setStatus(status);

        if ("1".equals(status)) {
            deviceService.handleUpdateDeviceStatus("DOOR-1", "OPEN");
        }

        else if ("0".equals(status)) {
            deviceService.handleUpdateDeviceStatus("DOOR-1", "CLOSE");
        }

        doorCommanndRepository.save(command);

        return new DoorCommandDTO(command);
    }

    // light color command
    public LightColorCommandDTO handleCreateLightColorCommand(String color, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        if ("OFF".equals(device.getStatus())) {
            throw new RuntimeException("Light is off!");
        }

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, color);

        // save to database
        LightCommand command = new LightCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setStatus("ON");
        command.setColor(color);
        lightCommandRepository.save(command);

        return new LightColorCommandDTO(command);
    }

    // light status command
    public LightStatusCommandDTO handleCreateLightStatusCommand(String status, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        // save to database
        LightCommand command = new LightCommand();

        // if status is on in the first time, it will set default color is one
        // else it still keep the previous color
        if ("ON".equals(status)) {
            if ("ON".equals(device.getStatus())) {
                throw new RuntimeException("Light is already on!");
            }

            String color = getLatestColor(device);

            if (color == null) {
                command.setColor("#FFFFFF");
                mqttPublisherService.publishCommand(topic, "#FFFFFF");
            }

            else {
                command.setColor(color);
                mqttPublisherService.publishCommand(topic, color);
            }
        }

        else if ("OFF".equals(status)) {
            if ("OFF".equals(device.getStatus())) {
                throw new RuntimeException("Light is already off!");
            }
            String color = getLatestColor(device);
            command.setColor(color);
            mqttPublisherService.publishCommand(topic, "#000000");
        }

        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setStatus(status);

        deviceService.handleUpdateDeviceStatus("LED-1", status);

        lightCommandRepository.save(command);

        return new LightStatusCommandDTO(command);
    }

    // get latest color
    public String getLatestColor(Device device) {
        return lightCommandRepository
                .findTopByDeviceOrderByTimestampDesc(device)
                .map(LightCommand::getColor)
                .orElse(null);
    }

    // automatic fan controll
    @Scheduled(fixedRate = 10000)
    public void autoControlLight() throws MqttException {
        Device device = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        Device sensor = deviceRepository.findByDeviceId("LIGHT-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));
        
        UserSetting setting = userSettingRepository.findByDevice(device)
                .orElse(new UserSetting());

        // if auto mode not turn on then this function will not work
        if (!setting.getAutoMode()) {
            return;
        }

        Float brightness = getLatestBrightness(sensor);

        if (brightness < 40) {
            handleAutoLightCommand(device, "ON");
        }

        else if (brightness > 70) {
            handleAutoLightCommand(device, "OFF");
        }
    }

    // handle fan command for auto mode
    public void handleAutoLightCommand(Device device, String status) throws MqttException {
        LightCommand command = new LightCommand();
        String topic = "itsmejoanro/feeds/bbc-led";

        if ("ON".equals(status)) {
            // save to database
            String color = getLatestColor(device);

            if (color == null) {
                command.setColor("#FFFFFF");
                mqttPublisherService.publishCommand(topic, "#FFFFFF");
            }

            else {
                command.setColor(color);
                mqttPublisherService.publishCommand(topic, color);
            }
        }

        else if ("OFF".equals(status)) {
            String color = getLatestColor(device);
            command.setColor(color);
            mqttPublisherService.publishCommand(topic, "#000000");
        }

        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setStatus(status);

        deviceService.handleUpdateDeviceStatus("FAN-1", status);

        lightCommandRepository.save(command);
    }

    // get latest brightness
    public Float getLatestBrightness(Device device) {
        return brightnessRepository
                .findTopByDeviceOrderByTimestampDesc(device)
                .map(Light::getBrightness)
                .orElse(null);
    }
}