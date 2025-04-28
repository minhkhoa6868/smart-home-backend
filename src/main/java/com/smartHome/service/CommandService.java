package com.smartHome.service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;
    private Float tempDistance = 0f;

    public CommandService(MqttPublisherService mqttPublisherService, FanCommandRepository fanCommandRepository,
            DoorCommanndRepository doorCommanndRepository, LightCommandRepository lightCommandRepository,
            DeviceRepository deviceRepository, DeviceService deviceService, BrightnessRepository brightnessRepository,
            CommandRepository commandRepository, UserRepository userRepository, DistanceRepository distanceRepository, 
            SimpMessagingTemplate messagingTemplate) {
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
        this.messagingTemplate = messagingTemplate;
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
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        command.setDevice(device);
        command.setUser(user);
        command.setTimestamp(now);
        command.setSpeed(speed);

        if ("0".equals(speed)) {
            if ("Off".equals(device.getStatus())) {
                throw new RuntimeException("Fan is already off!");
            }

            command.setStatus("Off");
            deviceService.handleUpdateDeviceStatus("FAN-1", "Off");
        }

        else if ("1".equals(speed) || "2".equals(speed) || "3".equals(speed)) {
            if ("On".equals(device.getStatus())) {
                throw new RuntimeException("Fan is already on!");
            }

            command.setStatus("On");
            deviceService.handleUpdateDeviceStatus("FAN-1", "On");
            deviceService.handleUpdateDeviceStartUsingTime("FAN-1", now);
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
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        command.setDevice(device);
        command.setUser(user);
        command.setTimestamp(now);

        if ("1".equals(status)) {
            if ("Open".equals(device.getStatus())) {
                throw new RuntimeException("Door is already open!");
            }

            command.setStatus("Open");
            deviceService.handleUpdateDeviceStatus("DOOR-1", "Open");
            deviceService.handleUpdateDeviceStartUsingTime("DOOR-1", now);
        }

        else if ("0".equals(status)) {
            if ("Close".equals(device.getStatus())) {
                throw new RuntimeException("Door is already close!");
            }

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
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        command.setDevice(device);
        command.setUser(user);
        command.setTimestamp(now);
        command.setStatus(status);

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

            deviceService.handleUpdateDeviceStartUsingTime("LED-1", now);
        }

        else if ("Off".equals(status)) {
            if ("Off".equals(device.getStatus())) {
                throw new RuntimeException("Light is already off!");
            }
            String color = getLatestColor();
            command.setColor(color);
            mqttPublisherService.publishCommand(topic, "#000000");
        }

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

    // turn on auto light mode
    public void handleAutoLightModeOn(ZonedDateTime startTime, ZonedDateTime endTime) {
        Device led = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        led.setAlertStartTime(startTime);
        led.setAlertEndTime(endTime);
        led.setIsAutoMode(true);

        deviceRepository.save(led);
    }

    // turn off auto light mode
    public void handleAutoLightModeOff() {
        Device led = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        led.setAlertStartTime(null);
        led.setAlertEndTime(null);
        led.setIsAutoMode(false);

        deviceRepository.save(led);
    }

    // automatic fan controll
    @Scheduled(fixedRate = 10000)
    public void autoControlLight() throws MqttException {
        Device led = deviceRepository.findByDeviceId("LED-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        Device sensor = deviceRepository.findByDeviceId("LIGHT-1")
                .orElseThrow(() -> new RuntimeException("Device not found!"));

        if (led.getAlertStartTime() != null && led.getAlertEndTime() != null) {
            ZonedDateTime startTime = led.getAlertStartTime();
            ZonedDateTime endTime = led.getAlertEndTime();
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            
            boolean isActive;
                
            if (startTime.isBefore(endTime)) {
                // Normal range (e.g. 8AM to 6PM)
                isActive = !now.isBefore(startTime) && !now.isAfter(endTime);
            } else {
                // Overnight range (e.g. 10PM to 6AM next day)
                isActive = !now.isBefore(startTime) || !now.isAfter(endTime);
            }
        
            led.setIsAutoMode(isActive);
        
            if (!isActive && now.isAfter(endTime)) {
                // Reset time range after it's done
                led.setAlertStartTime(null);
                led.setAlertEndTime(null);
            }
        
            deviceRepository.save(led);
        }

        // if auto mode not turn on then this function will not work
        if (!led.getIsAutoMode()) {
            return;
        }

        Float brightness = getLatestBrightness(sensor);

        if (brightness < 40) {
            handleAutoLightCommand(led, "On");
        }

        else if (brightness > 70) {
            handleAutoLightCommand(led, "Off");
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

    // turn on security mode
    public void handleSecurityModeOn(ZonedDateTime startTime, ZonedDateTime endTime) {
        Device sensor = deviceRepository.findByDeviceId("DISTANCE-1")
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        sensor.setAlertStartTime(startTime);
        sensor.setAlertEndTime(endTime);
        sensor.setIsAutoMode(true);

        deviceRepository.save(sensor);
    }

    // turn off security mode
    public void handleSecurityModeOff() {
        Device sensor = deviceRepository.findByDeviceId("DISTANCE-1")
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        sensor.setAlertStartTime(null);
        sensor.setAlertEndTime(null);
        sensor.setIsAutoMode(false);

        deviceRepository.save(sensor);
    }

    // handle alert by distance
    @Scheduled(fixedRate = 30000)
    public void handleAlert() {
        Device sensor = deviceRepository.findByDeviceId("DISTANCE-1")
            .orElseThrow(() -> new RuntimeException("Device not found!"));

        if (sensor.getAlertStartTime() != null && sensor.getAlertEndTime() != null) {
            ZonedDateTime startTime = sensor.getAlertStartTime();
            ZonedDateTime endTime = sensor.getAlertEndTime();
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    
            boolean isActive;
        
            if (startTime.isBefore(endTime)) {
                // Normal range (e.g. 8AM to 6PM)
                isActive = !now.isBefore(startTime) && !now.isAfter(endTime);
            } else {
                // Overnight range (e.g. 10PM to 6AM next day)
                isActive = !now.isBefore(startTime) || !now.isAfter(endTime);
            }

            sensor.setIsAutoMode(isActive);

            if (!isActive && now.isAfter(endTime)) {
                // Reset time range after it's done
                sensor.setAlertStartTime(null);
                sensor.setAlertEndTime(null);
            }

            deviceRepository.save(sensor);
        }

        if (!sensor.getIsAutoMode()) {
            return;
        }

        Float distance = getLatestDistance(sensor);

        if (distance <= tempDistance) {
            messagingTemplate.convertAndSend("/topic/alert", Map.of(
                "alert", true,
                "message", "Something strange"
            ));
        }

        tempDistance = distance;
    }

    // get latest distance
    public Float getLatestDistance(Device device) {
        return distanceRepository
                .findTopByDeviceOrderByTimestampDesc(device)
                .map(Distance::getDistance)
                .orElse(null);
    }

    // calculate power consumption
    @Scheduled(fixedRate = 1000)
    public void calculatePowerConsumption() {
        List<String> deviceIds = List.of("FAN-1", "LED-1", "DOOR-1", "DTH-1", "LIGHT-1", "DISTANCE-1");

        Map<String, Device> devices = deviceIds.stream()
            .map(id -> deviceRepository.findByDeviceId(id)
                .orElseThrow(() -> new RuntimeException("Device not found: " + id)))
            .collect(Collectors.toMap(Device::getDeviceId, d -> d));

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        for (Device device : devices.values()) {
            String status = device.getStatus();
            boolean isUsing = status.equals("On") || (device.getDeviceId().equals("DOOR-1") && status.equals("Open"));

            if (isUsing && device.getStartUsingTime() != null) {
                Duration duration = Duration.between(device.getStartUsingTime(), now);
                double hours = duration.toMillis() / 3600000.0;

                // Use double precision for power consumption
                double powerConsumption = hours * device.getPower() / 1000;

                device.setPowerConsume(powerConsumption);
                deviceRepository.save(device);
            }
        }

        double totalPowerConsumption = devices.values().stream()
            .mapToDouble(Device::getPowerConsume)
            .sum();

        messagingTemplate.convertAndSend("/topic/power", Map.of(
            "power", totalPowerConsumption
        ));
    }

    // handle reset power consumption
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    public void handleResetPowerConsumption() {
        List<String> deviceIds = List.of("FAN-1", "LED-1", "DOOR-1", "DTH-1", "LIGHT-1", "DISTANCE-1");

        for (String deviceId : deviceIds) {
            Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));

            device.setPowerConsume(0D);
            device.setStartUsingTime(null);
            deviceRepository.save(device);
        }
    }
}