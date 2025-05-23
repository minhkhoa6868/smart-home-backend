package com.smartHome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findByDeviceId(String deviceId);
} 
