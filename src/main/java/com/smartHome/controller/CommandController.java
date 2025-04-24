package com.smartHome.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.dto.CommandDTO;
import com.smartHome.dto.DoorCommandDTO;
import com.smartHome.dto.FanCommandDTO;
import com.smartHome.dto.LightColorCommandDTO;
import com.smartHome.dto.LightStatusCommandDTO;
import com.smartHome.dto.TimeRangeDTO;
// import com.smartHome.model.CommandType.DoorCommand;
// import com.smartHome.model.CommandType.FanCommand;
// import com.smartHome.model.CommandType.LightCommand;
import com.smartHome.service.CommandService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/commands")
public class CommandController {
    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/fan")
    public ResponseEntity<FanCommandDTO> sendFanCommand(@RequestBody FanCommandDTO command) throws Exception {
        FanCommandDTO newCommand = commandService.handleCreateFanCommand(command.getSpeed(), command.getUserId(), "itsmejoanro/feeds/bbc-fan");

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }

    @PostMapping("/door")
    public ResponseEntity<DoorCommandDTO> sendDoorCommand(@RequestBody DoorCommandDTO command) throws Exception {
        DoorCommandDTO newCommand = commandService.handleCreateDoorCommand(command.getStatus(), command.getUserId(), "itsmejoanro/feeds/bbc-door");

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }

    @PostMapping("/light/color")
    public ResponseEntity<LightColorCommandDTO> sendLightColorCommand(@RequestBody LightColorCommandDTO command) throws Exception {
        LightColorCommandDTO newCommand = commandService.handleCreateLightColorCommand(command.getColor(), command.getUserId(), "itsmejoanro/feeds/bbc-led");

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }

    @PostMapping("/light/status")
    public ResponseEntity<LightStatusCommandDTO> sendLightStatusCommand(@RequestBody LightStatusCommandDTO command) throws Exception {
        LightStatusCommandDTO newCommand = commandService.handleCreateLightStatusCommand(command.getStatus(), command.getUserId(), "itsmejoanro/feeds/bbc-led");

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }

    @PostMapping("/light/auto-mode")
    public String setLightAutoMode(@RequestBody TimeRangeDTO timeRangeDTO) {
        commandService.handleAutoLightMode(timeRangeDTO.getStarTime(), timeRangeDTO.getEndTime());
        
        return "Light auto mode is on";
    }

    @PostMapping("/security-mode")
    public String setSecurityMode(@RequestBody TimeRangeDTO timeRangeDTO) {
        commandService.handleSecurityMode(timeRangeDTO.getStarTime(), timeRangeDTO.getEndTime());

        return "Security mode is on";
    }
    
    
    @GetMapping("/fan/latest")
    public ResponseEntity<Map<String, String>> getLatestFanCommand() {
        Map<String, String> result = new HashMap<>();
        result.put("speed", commandService.getLatestSpeed());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/light/latest")
    public ResponseEntity<Map<String, String>> getLatestLightCommand() {
        Map<String, String> result = new HashMap<>();
        result.put("color", commandService.getLatestColor());
        return ResponseEntity.ok(result);
    }
    

    @GetMapping("/five-latest")
    public ResponseEntity<List<CommandDTO>> get5LatestCommand() {
        return ResponseEntity.ok(commandService.handleGet5LatestCommand());
    }

    @GetMapping("/all")
    public ResponseEntity<List<CommandDTO>> getAllCommand() {
        return ResponseEntity.ok(commandService.handleGetAllCommand());
    }
}