package com.smartHome.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartHome.model.Command;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
    List<Command> findTop5ByOrderByTimestampDesc();
    List<Command> findAllByOrderByTimestampDesc();
}
