package com.smartHome.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.CommandType.DoorCommand;

public interface DoorCommanndRepository extends JpaRepository<DoorCommand, Long> {
    
}
