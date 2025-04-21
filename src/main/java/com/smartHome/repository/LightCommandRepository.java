package com.smartHome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.Device;
import com.smartHome.model.CommandType.LightCommand;

public interface LightCommandRepository extends JpaRepository<LightCommand, Long> {
    Optional<LightCommand> findTopByDeviceOrderByTimestampDesc(Device device);
}
