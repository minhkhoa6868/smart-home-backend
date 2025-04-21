package com.smartHome.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.Record;

public interface RecordRepository extends JpaRepository<Record, Long> {

}
