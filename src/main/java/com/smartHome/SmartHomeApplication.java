package com.smartHome;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@EnableScheduling
@SpringBootApplication
public class SmartHomeApplication {
	@Bean(destroyMethod = "disconnect")
	public IMqttClient mqttClient() throws MqttException {
		Dotenv dotenv = Dotenv.load();

		String publisherId = UUID.randomUUID().toString();
		String broker = "ssl://io.adafruit.com:8883";

		IMqttClient client = new MqttClient(broker, publisherId);

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(dotenv.get("ADAFRUIT_USERNAME"));
		options.setPassword(dotenv.get("ADAFRUIT_IO_KEY").toCharArray());
		options.setCleanSession(true);

		System.out.println("Is connected? " + client.isConnected());
		client.connect(options);
		System.out.println("Is connected after connect()? " + client.isConnected());


		return client;
	}

	public static void main(String[] args) {
		SpringApplication.run(SmartHomeApplication.class, args);
	}
}
