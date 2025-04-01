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
import com.smartHome.model.RecordType.Temperature;
import com.smartHome.repository.DeviceRepository;
import com.smartHome.repository.DoorCommanndRepository;
import com.smartHome.repository.FanCommandRepository;
import com.smartHome.repository.LightCommandRepository;
import com.smartHome.repository.TemperatureRepository;
import com.smartHome.repository.UserSettingRepository;

@Service
public class CommandService {
    private final MqttPublisherService mqttPublisherService;
    private final FanCommandRepository fanCommandRepository;
    private final DoorCommanndRepository doorCommanndRepository;
    private final LightCommandRepository lightCommandRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceService deviceService;
    private final TemperatureRepository temperatureRepository;
    private final UserSettingRepository userSettingRepository;

    public CommandService(MqttPublisherService mqttPublisherService, FanCommandRepository fanCommandRepository,
            DoorCommanndRepository doorCommanndRepository, LightCommandRepository lightCommandRepository,
            DeviceRepository deviceRepository, DeviceService deviceService, TemperatureRepository temperatureRepository,
            UserSettingRepository userSettingRepository) {
        this.mqttPublisherService = mqttPublisherService;
        this.fanCommandRepository = fanCommandRepository;
        this.doorCommanndRepository = doorCommanndRepository;
        this.lightCommandRepository = lightCommandRepository;
        this.deviceRepository = deviceRepository;
        this.deviceService = deviceService;
        this.temperatureRepository = temperatureRepository;
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

        if (speed == "0") {
            deviceService.handleUpdateDeviceStatus("FAN-1", "OFF");
        }

        else {
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

        if (status == "1") {
            deviceService.handleUpdateDeviceStatus("DOOR-1", "OPEN");
        }

        else if (status == "0") {
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
    public void autoControlFan() throws MqttException {
        Device device = deviceRepository.findByDeviceId("FAN-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        Device sensor = deviceRepository.findByDeviceId("DTH-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));
        
        UserSetting setting = userSettingRepository.findByDevice(device)
                .orElse(new UserSetting());

        // if auto mode not turn on then this function will not work
        if (!setting.getAutoMode()) {
            return;
        }

        Float temperature = getLatestTemperature(sensor);
        System.out.println("temperature: " + temperature);
        Float desireTemperature = setting.getDesireTemperature();
        System.out.println("desire temperature: " + desireTemperature);

        // if temp > temp user want + 2 then turn on the fan with speed 3
        if (temperature > desireTemperature + 2) {
            handleAutoFanCommand("3", device, "ON");
        }

        // if temp > temp user want then turn on the fan with speed 2
        else if (temperature > desireTemperature + 1) {
            handleAutoFanCommand("2", device, "ON");
        }

        else if (temperature > desireTemperature) {
            handleAutoFanCommand("1", device, "ON");
        }

        // if smaller then turn off
        else if (temperature <= desireTemperature) {
            handleAutoFanCommand("0", device, "OFF");
        }
    }

    // handle fan command for auto mode
    public void handleAutoFanCommand(String speed, Device device, String status) throws MqttException {
        // publish fan command over MQTT
        mqttPublisherService.publishCommand("itsmejoanro/feeds/bbc-fan", speed);

        // save to database
        FanCommand command = new FanCommand();
        command.setDevice(device);
        command.setTimestamp(LocalDateTime.now());
        command.setSpeed(speed);

        deviceService.handleUpdateDeviceStatus("FAN-1", status);

        fanCommandRepository.save(command);
    }

    // get latest temperature
    public Float getLatestTemperature(Device device) {
        return temperatureRepository
                .findTopByDeviceOrderByTimestampDesc(device)
                .map(Temperature::getTemperature)
                .orElse(null);
    }
}