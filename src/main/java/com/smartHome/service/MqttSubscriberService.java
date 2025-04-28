package com.smartHome.service;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.smartHome.dto.RecordDTO;
import com.smartHome.model.Device;
import com.smartHome.repository.DeviceRepository;
import com.smartHome.model.RecordType.Distance;
import com.smartHome.model.RecordType.Humidity;
import com.smartHome.model.RecordType.Light;
import com.smartHome.model.RecordType.Temperature;
import com.smartHome.repository.RecordRepository;

import jakarta.annotation.PostConstruct;

@Service
public class MqttSubscriberService {
    private final IMqttClient mqttClient;
    private final RecordRepository recordRepository;
    private final DeviceRepository deviceRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MqttSubscriberService(RecordRepository recordRepository, DeviceRepository deviceRepository, IMqttClient iMqttClient, SimpMessagingTemplate simpMessagingTemplate) throws Exception {
        this.recordRepository = recordRepository;
        this.deviceRepository = deviceRepository;
        this.mqttClient = iMqttClient;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostConstruct
    public void init() throws Exception {
        try {
            if (!mqttClient.isConnected()) {
                System.err.println("MQTT Client is not connected!");
                mqttClient.reconnect();
                System.out.println("Reconnected MQTT client!");
            }
    
            System.out.println("Starting subscriptions...");
            List<String> deviceIds = List.of("DTH-1", "LIGHT-1", "DISTANCE-1");

            Map<String, Device> devices = deviceIds.stream()
            .map(id -> deviceRepository.findByDeviceId(id)
                .orElseThrow(() -> new RuntimeException("Device not found: " + id)))
            .collect(Collectors.toMap(Device::getDeviceId, d -> d));

            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            
            for (Device device : devices.values()) {
                device.setStartUsingTime(now);
            }
            
            subscribe("itsmejoanro/feeds/bbc-distance");
            subscribe("itsmejoanro/feeds/bbc-temp");
            subscribe("itsmejoanro/feeds/bbc-humid");
            subscribe("itsmejoanro/feeds/bbc-light");
    
        } catch (Exception e) {
            System.err.println("Error in MQTT init: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void subscribe(String topics) throws Exception {
        mqttClient.subscribe(topics, (topic,msg) -> {
            String payload = new String(msg.getPayload());
            System.out.println("Received message: " + payload + "from topic" + topics);

            try {
                Float value = Float.parseFloat(payload);

                System.out.println("Value: " + value);    

                Device device;

                ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
                switch (topics) {
                    case "itsmejoanro/feeds/bbc-humid":
                        device = deviceRepository.findById("DTH-1").orElse(null);
                        Humidity humidity = new Humidity();
                        humidity.setDevice(device);
                        humidity.setTimestamp(timestamp);
                        humidity.setTemperature(value);
                        recordRepository.save(humidity);

                        RecordDTO humidityDTO = new RecordDTO();
                        humidityDTO.setTimestamp(timestamp);
                        humidityDTO.setHumidity(value);
                        simpMessagingTemplate.convertAndSend("/topic/humidity", humidityDTO);
                        break;
                
                    case "itsmejoanro/feeds/bbc-temp":
                        device = deviceRepository.findById("DTH-1").orElse(null);
                        Temperature temperature = new Temperature();
                        temperature.setDevice(device);
                        temperature.setTimestamp(timestamp);
                        temperature.setTemperature(value);
                        recordRepository.save(temperature);

                        RecordDTO temperatureDTO = new RecordDTO();
                        temperatureDTO.setTimestamp(timestamp);
                        temperatureDTO.setTemperature(value);
                        simpMessagingTemplate.convertAndSend("/topic/temperature", temperatureDTO);
                        break;

                    case "itsmejoanro/feeds/bbc-distance":
                        device = deviceRepository.findById("DISTANCE-1").orElse(null);
                        Distance motion = new Distance();
                        motion.setDevice(device);
                        motion.setTimestamp(timestamp);
                        motion.setDistance(value);
                        recordRepository.save(motion);

                        RecordDTO distanceDTO = new RecordDTO();
                        distanceDTO.setTimestamp(timestamp);
                        distanceDTO.setMotion(value);
                        simpMessagingTemplate.convertAndSend("/topic/distance", distanceDTO);
                        break;

                    case "itsmejoanro/feeds/bbc-light":
                        device = deviceRepository.findById("LIGHT-1").orElse(null);
                        Light light = new Light();
                        light.setDevice(device);
                        light.setTimestamp(timestamp);
                        light.setBrightness(value);
                        recordRepository.save(light);

                        RecordDTO brightnessDTO = new RecordDTO();
                        brightnessDTO.setTimestamp(timestamp);
                        brightnessDTO.setBrightness(value);
                        simpMessagingTemplate.convertAndSend("/topic/light", brightnessDTO);
                        break;

                    default:
                        throw new Exception("Invalid topic: " + topics);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}