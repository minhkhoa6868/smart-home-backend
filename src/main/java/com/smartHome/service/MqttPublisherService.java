package com.smartHome.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttPublisherService {
    private final IMqttClient mqttClient;

    public MqttPublisherService() throws MqttException {
        String publisherId = "spring-boot-server";
        String brokerUrl = "tcp://localhost:1883";
        mqttClient = new MqttClient(brokerUrl, publisherId);
        mqttClient.connect();
    }

    public void publishCommand(Long deviceId, String signal, String type) throws MqttException {
        String topic = "device/" + deviceId + "/command";
        String payload = signal + ":" + type;
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);

        mqttClient.publish(topic, message);
        System.out.println("Sent to device: " + topic + " -> " + payload);
    }
}
