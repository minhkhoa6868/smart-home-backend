package com.smartHome.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.CommandType.FanCommand;

public interface FanCommandRepository extends JpaRepository<FanCommand, Long> {
    
}
