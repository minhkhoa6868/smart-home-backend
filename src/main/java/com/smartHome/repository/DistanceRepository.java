package com.smartHome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.Device;
import com.smartHome.model.RecordType.Distance;

public interface DistanceRepository extends JpaRepository<Distance, Long> {
    Optional<Distance> findTopByDeviceOrderByTimestampDesc(Device device);
}
