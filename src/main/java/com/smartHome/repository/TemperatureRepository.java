package com.smartHome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.Device;
import com.smartHome.model.RecordType.Temperature;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
    Optional<Temperature> findTopByDeviceOrderByTimestampDesc(Device device);
}
