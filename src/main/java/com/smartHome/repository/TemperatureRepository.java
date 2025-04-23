package com.smartHome.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartHome.model.RecordType.Temperature;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
    @Query("SELECT h FROM Temperature h WHERE h.timestamp BETWEEN :start AND :end ORDER BY h.timestamp ASC")
    List<Temperature> findByTimestampBetween(
        @Param("start") ZonedDateTime start,
        @Param("end") ZonedDateTime end
    );
}
