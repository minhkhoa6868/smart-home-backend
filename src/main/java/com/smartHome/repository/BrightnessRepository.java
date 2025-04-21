package com.smartHome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.Device;
import com.smartHome.model.RecordType.Light;

public interface BrightnessRepository extends JpaRepository<Light, Long> {
    Optional<Light> findTopByDeviceOrderByTimestampDesc(Device device);
}
