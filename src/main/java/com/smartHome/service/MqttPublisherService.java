package com.smartHome.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisherService {
    private final IMqttClient mqttClient;

    public MqttPublisherService(IMqttClient iMqttClient) throws MqttException {
        this.mqttClient = iMqttClient;
    }

    public void publishCommand(String topic, String payload) throws MqttException {
        try {
            if (!mqttClient.isConnected()) {
                System.err.println("MQTT Client is not connected. Reconnecting...");
                mqttClient.reconnect();
            }

            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            message.setRetained(false);


            mqttClient.publish(topic, message);
            System.out.println("Sent to device: " + topic + " -> " + payload);

        } catch (MqttException e) {
            System.err.println("Failed to publish message to topic " + topic + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
