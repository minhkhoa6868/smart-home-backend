package com.smartHome.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.smartHome.model.Record;
import com.smartHome.model.RecordType.Humidity;
import com.smartHome.model.RecordType.Temperature;
import com.smartHome.dto.HumidityDTO;
import com.smartHome.dto.RecordDTO;
import com.smartHome.dto.TemperatureDTO;
import com.smartHome.repository.HumidityRepository;
import com.smartHome.repository.RecordRepository;
import com.smartHome.repository.TemperatureRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RecordService {
    private final RecordRepository recordRepository;
    private final HumidityRepository humidityRepository;
    private final TemperatureRepository temperatureRepository;

    public RecordService(RecordRepository recordRepository, HumidityRepository humidityRepository, TemperatureRepository temperatureRepository) {
        this.recordRepository = recordRepository;
        this.humidityRepository = humidityRepository;
        this.temperatureRepository = temperatureRepository;
    }

    // get all records
    public List<RecordDTO> handleGetAllRecords() {
        List<Record> records = recordRepository.findAll();

        return records.stream()
              .map(RecordDTO::new)
              .collect(Collectors.toList());
    }

    // get record by id
    public RecordDTO handleGetOneRecord(Long recordId) {
        Record record = recordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Record not found"));
        return new RecordDTO(record);
    } 

    // get data of each hour to plot diagram
    public List<HumidityDTO> handleGetTodayHumidity() {
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");

        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(zone);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        List<Humidity> records = humidityRepository.findByTimestampBetween(startOfDay, endOfDay);

        Map<Integer, List<Humidity>> grouped = records.stream()
            .collect(Collectors.groupingBy(r -> r.getTimestamp().getHour()));

        List<HumidityDTO> hourlyData = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            ZonedDateTime hourTime = startOfDay.plusHours(hour);
            List<Humidity> hourRecords = grouped.getOrDefault(hour, List.of());

            if (!hourRecords.isEmpty()) {
                // Average humidity of the hour
                double avg = hourRecords.stream()
                    .mapToDouble(Humidity::getTemperature)
                    .average()
                    .orElse(0.0);
                hourlyData.add(new HumidityDTO(hourTime, (float) avg));
            } else {
                hourlyData.add(new HumidityDTO(hourTime, null));
            }
        }

        return hourlyData;
    }

    // get data of each hour to plot diagram
    public List<TemperatureDTO> handleGetTodayTemperature() {
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");

        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(zone);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        List<Temperature> records = temperatureRepository.findByTimestampBetween(startOfDay, endOfDay);

        Map<Integer, List<Temperature>> grouped = records.stream()
            .collect(Collectors.groupingBy(r -> r.getTimestamp().getHour()));

        List<TemperatureDTO> hourlyData = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            ZonedDateTime hourTime = startOfDay.plusHours(hour);
            List<Temperature> hourRecords = grouped.getOrDefault(hour, List.of());

            if (!hourRecords.isEmpty()) {
                // Average humidity of the hour
                double avg = hourRecords.stream()
                    .mapToDouble(Temperature::getTemperature)
                    .average()
                    .orElse(0.0);
                hourlyData.add(new TemperatureDTO(hourTime, (float) avg));
            } else {
                hourlyData.add(new TemperatureDTO(hourTime, null));
            }
        }

        return hourlyData;
    }
}