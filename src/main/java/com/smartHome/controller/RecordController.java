package com.smartHome.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.dto.RecordDTO;
import com.smartHome.service.RecordService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/records")
public class RecordController {
    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<RecordDTO> getOneRecord(@PathVariable Long recordId) {
        RecordDTO newRecord = recordService.handleGetOneRecord(recordId);

        return ResponseEntity.ok(newRecord);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RecordDTO>> getAllRecords() {
        List<RecordDTO> records = recordService.handleGetAllRecords();
        return ResponseEntity.ok(records);
    }
}