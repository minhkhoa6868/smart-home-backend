package com.smartHome.service;

import java.time.LocalDateTime;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartHome.dto.IncommingRecordDTO;
import com.smartHome.model.Device;
import com.smartHome.repository.DeviceRepository;
import com.smartHome.model.Record;
import com.smartHome.model.RecordType.Humidity;
import com.smartHome.model.RecordType.Light;
import com.smartHome.model.RecordType.Motion;
import com.smartHome.model.RecordType.Temperature;
import com.smartHome.repository.RecordRepository;

import jakarta.annotation.PostConstruct;

@Service
public class MqttSubscriberService {
    private final IMqttClient mqttClient;
    private final RecordRepository recordRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;

    public MqttSubscriberService(RecordRepository recordRepository, DeviceRepository deviceRepository, IMqttClient iMqttClient) throws Exception {
        this.recordRepository = recordRepository;
        this.deviceRepository = deviceRepository;
        this.mqttClient = iMqttClient;
        this.objectMapper = new ObjectMapper();
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
            System.out.println("Received message: " + payload);

            try {
                // You could create a DTO and map it manually, but for simplicity:
                IncommingRecordDTO incomingRecord = objectMapper.readValue(payload, IncommingRecordDTO.class);

                // Lookup device if not already attached
                Device device = deviceRepository.findById(incomingRecord.getDeviceId())
                        .orElseThrow(() -> new RuntimeException("Device not found"));

                Record record = createRecordByType(incomingRecord, device);
                
                recordRepository.save(record);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Record createRecordByType(IncommingRecordDTO incommingRecord, Device device) {
        LocalDateTime timestamp = incommingRecord.getTimestamp() != null ? incommingRecord.getTimestamp() : LocalDateTime.now();
        
        switch (incommingRecord.getRecordType().toUpperCase()) {
            case "HUMIDITY":
                Humidity humidity = new Humidity();
                humidity.setDevice(device);
                humidity.setTimestamp(timestamp);
                humidity.setTemperature(incommingRecord.getHumidity());
                return humidity;

            case "TEMPERATURE":
                Temperature temperature = new Temperature();
                temperature.setDevice(device);
                temperature.setTimestamp(timestamp);
                temperature.setTemperature(incommingRecord.getTemperature());
                return temperature;

            case "LIGHT":
                Light light = new Light();
                light.setDevice(device);
                light.setTimestamp(timestamp);
                light.setBrightness(incommingRecord.getBrightness());
                return light;

            case "MOTION":
                Motion motion = new Motion();
                motion.setDevice(device);
                motion.setTimestamp(timestamp);
                motion.setMotion(incommingRecord.getMotion());
                return motion;

            default:
                throw new IllegalArgumentException("Unknown record type: " + incommingRecord.getRecordType());
        }
    }
}