package com.smartHome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.Device;
import com.smartHome.model.CommandType.FanCommand;

public interface FanCommandRepository extends JpaRepository<FanCommand, Long> {
    Optional<FanCommand> findTopByDeviceOrderByTimestampDesc(Device device);
}
