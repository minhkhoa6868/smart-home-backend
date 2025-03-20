package com.smartHome.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.smartHome.model.Record;
import com.smartHome.dto.RecordDTO;
import com.smartHome.repository.RecordRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RecordService {
    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
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
}