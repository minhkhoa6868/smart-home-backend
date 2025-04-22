package com.smartHome.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.smartHome.dto.CommandDTO;
import com.smartHome.dto.DoorCommandDTO;
import com.smartHome.dto.FanCommandDTO;
import com.smartHome.dto.LightColorCommandDTO;
import com.smartHome.dto.LightStatusCommandDTO;
import com.smartHome.model.Device;
import com.smartHome.model.User;
import com.smartHome.model.CommandType.DoorCommand;
import com.smartHome.model.CommandType.FanCommand;
import com.smartHome.model.CommandType.LightCommand;
import com.smartHome.model.RecordType.Distance;
import com.smartHome.model.RecordType.Light;
import com.smartHome.repository.BrightnessRepository;
import com.smartHome.repository.CommandRepository;
import com.smartHome.repository.DeviceRepository;
import com.smartHome.repository.DistanceRepository;
import com.smartHome.repository.DoorCommanndRepository;
import com.smartHome.repository.FanCommandRepository;
import com.smartHome.repository.LightCommandRepository;
import com.smartHome.repository.UserRepository;

@Service
public class CommandService {
    private final MqttPublisherService mqttPublisherService;
    private final FanCommandRepository fanCommandRepository;
    private final DoorCommanndRepository doorCommanndRepository;
    private final LightCommandRepository lightCommandRepository;
    private final CommandRepository commandRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DeviceService deviceService;
    private final BrightnessRepository brightnessRepository;
    private final DistanceRepository distanceRepository;
    private Float tempDistance = 0f;

    public CommandService(MqttPublisherService mqttPublisherService, FanCommandRepository fanCommandRepository,
            DoorCommanndRepository doorCommanndRepository, LightCommandRepository lightCommandRepository,
            DeviceRepository deviceRepository, DeviceService deviceService, BrightnessRepository brightnessRepository,
            CommandRepository commandRepository, UserRepository userRepository, DistanceRepository distanceRepository) {
        this.mqttPublisherService = mqttPublisherService;
        this.fanCommandRepository = fanCommandRepository;
        this.doorCommanndRepository = doorCommanndRepository;
        this.lightCommandRepository = lightCommandRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.deviceService = deviceService;
        this.brightnessRepository = brightnessRepository;
        this.commandRepository = commandRepository;
        this.distanceRepository = distanceRepository;
    }

    // get five latest command
    public List<CommandDTO> handleGet5LatestCommand() {
        return commandRepository.findTop5ByOrderByTimestampDesc()
                .stream()
                .map(CommandDTO::new)
                .collect(Collectors.toList());
    }

    // get all command
    public List<CommandDTO> handleGetAllCommand() {
        return commandRepository.findAllByOrderByTimestampDesc()
                .stream()
                .map(CommandDTO::new)
                .collect(Collectors.toList());
    }

    // fan command
    public FanCommandDTO handleCreateFanCommand(String speed, Long userId, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("FAN-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, speed);

        // save to database
        FanCommand command = new FanCommand();
        command.setDevice(device);
        command.setUser(user);
        command.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        command.setSpeed(speed);

        if ("0".equals(speed)) {
            command.setStatus("Off");
            deviceService.handleUpdateDeviceStatus("FAN-1", "Off");
        }

        else if ("1".equals(speed) || "2".equals(speed) || "3".equals(speed)) {
            command.setStatus("On");
            deviceService.handleUpdateDeviceStatus("FAN-1", "On");
        }

        fanCommandRepository.save(command);

        return new FanCommandDTO(command);
    }

    // get latest fan command
    public String getLatestSpeed() {
        Device fan = deviceRepository.findByDeviceId("FAN-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        return fanCommandRepository
                    .findTopByDeviceOrderByTimestampDesc(fan)
                    .map(FanCommand::getSpeed)
                    .orElse("Device not found!");
    }

    // Door command
    public DoorCommandDTO handleCreateDoorCommand(String status, Long userId, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("DOOR-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, status);

        // save to database
        DoorCommand command = new DoorCommand();
        command.setDevice(device);
        command.setUser(user);
        command.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        if ("1".equals(status)) {
            command.setStatus("Open");
            deviceService.handleUpdateDeviceStatus("DOOR-1", "Open");
        }

        else if ("0".equals(status)) {
            command.setStatus("Close");
            deviceService.handleUpdateDeviceStatus("DOOR-1", "Close");
        }

        doorCommanndRepository.save(command);

        return new DoorCommandDTO(command);
    }

    // light color command
    public LightColorCommandDTO handleCreateLightColorCommand(String color, Long userId, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if ("Off".equals(device.getStatus())) {
            throw new RuntimeException("Light is off!");
        }

        // publish fan command over MQTT
        mqttPublisherService.publishCommand(topic, color);

        // save to database
        LightCommand command = new LightCommand();
        command.setDevice(device);
        command.setUser(user);
        command.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        command.setStatus("On");
        command.setColor(color);
        lightCommandRepository.save(command);

        return new LightColorCommandDTO(command);
    }

    // light status command
    public LightStatusCommandDTO handleCreateLightStatusCommand(String status, Long userId, String topic) throws MqttException {
        Device device = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // save to database
        LightCommand command = new LightCommand();

        // if status is on in the first time, it will set default color is one
        // else it still keep the previous color
        if ("On".equals(status)) {
            if ("On".equals(device.getStatus())) {
                throw new RuntimeException("Light is already on!");
            }

            String color = getLatestColor();

            if (color == null) {
                command.setColor("#FFFFFF");
                mqttPublisherService.publishCommand(topic, "#FFFFFF");
            }

            else {
                command.setColor(color);
                mqttPublisherService.publishCommand(topic, color);
            }
        }

        else if ("Off".equals(status)) {
            if ("Off".equals(device.getStatus())) {
                throw new RuntimeException("Light is already off!");
            }
            String color = getLatestColor();
            command.setColor(color);
            mqttPublisherService.publishCommand(topic, "#000000");
        }

        command.setDevice(device);
        command.setUser(user);
        command.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        command.setStatus(status);

        deviceService.handleUpdateDeviceStatus("LED-1", status);

        lightCommandRepository.save(command);

        return new LightStatusCommandDTO(command);
    }

    // get latest color
    public String getLatestColor() {
        Device led = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        return lightCommandRepository
                .findTopByDeviceOrderByTimestampDesc(led)
                .map(LightCommand::getColor)
                .orElse(null);
    }

    // turn on auto mode
    public void handleAutoMode(Boolean isAutoMode) {
        Device device = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        device.setIsAutoMode(isAutoMode);
    }

    // automatic fan controll
    @Scheduled(fixedRate = 10000)
    public void autoControlLight() throws MqttException {
        Device device = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        Device sensor = deviceRepository.findByDeviceId("LIGHT-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        // if auto mode not turn on then this function will not work
        if (!device.getIsAutoMode()) {
            return;
        }

        Float brightness = getLatestBrightness(sensor);

        if (brightness < 40) {
            handleAutoLightCommand(device, "On");
        }

        else if (brightness > 70) {
            handleAutoLightCommand(device, "Off");
        }
    }

    // handle light command for auto mode
    public void handleAutoLightCommand(Device device, String status) throws MqttException {
        LightCommand command = new LightCommand();
        String topic = "itsmejoanro/feeds/bbc-led";

        if ("On".equals(status)) {
            // save to database
            String color = getLatestColor();

            if (color == null) {
                command.setColor("#FFFFFF");
                mqttPublisherService.publishCommand(topic, "#FFFFFF");
            }

            else {
                command.setColor(color);
                mqttPublisherService.publishCommand(topic, color);
            }
        }

        else if ("Off".equals(status)) {
            String color = getLatestColor();
            command.setColor(color);
            mqttPublisherService.publishCommand(topic, "#000000");
        }

        command.setDevice(device);
        command.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        command.setStatus(status);

        deviceService.handleUpdateDeviceStatus("LED-1", status);

        lightCommandRepository.save(command);
    }

    // get latest brightness
    public Float getLatestBrightness(Device device) {
        return brightnessRepository
                .findTopByDeviceOrderByTimestampDesc(device)
                .map(Light::getBrightness)
                .orElse(null);
    }

    // handle alert by distance
    @Scheduled(fixedRate = 60000)
    public boolean handleAlert() {
        Device sensor = deviceRepository.findByDeviceId("DISTANCE-1")
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        Float distance = getLatestDistance(sensor);

        if (distance <= tempDistance) {
            return true;
        }

        else {
            tempDistance = distance;
            return false;
        }
    }

    // get latest distance
    public Float getLatestDistance(Device device) {
        return distanceRepository
                .findTopByDeviceOrderByTimestampDesc(device)
                .map(Distance::getDistance)
                .orElse(null);
    }
}