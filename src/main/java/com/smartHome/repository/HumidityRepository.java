package com.smartHome.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartHome.model.RecordType.Humidity;

public interface HumidityRepository extends JpaRepository<Humidity, Long> {
    @Query("SELECT h FROM Humidity h WHERE h.timestamp BETWEEN :start AND :end ORDER BY h.timestamp ASC")
    List<Humidity> findByTimestampBetween(
        @Param("start") ZonedDateTime start,
        @Param("end") ZonedDateTime end
    );

}
