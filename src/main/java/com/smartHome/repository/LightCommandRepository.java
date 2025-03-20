package com.smartHome.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartHome.model.CommandType.LightCommand;

public interface LightCommandRepository extends JpaRepository<LightCommand, Long> {

}
