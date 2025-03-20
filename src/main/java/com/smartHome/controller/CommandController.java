package com.smartHome.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.model.CommandType.DoorCommand;
import com.smartHome.model.CommandType.FanCommand;
import com.smartHome.model.CommandType.LightCommand;
import com.smartHome.model.CommandType.LightCommand.Color;
import com.smartHome.service.CommandService;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/commands")
public class CommandController {
    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/fan")
    public ResponseEntity<FanCommand> sendFanCommand(@RequestBody FanCommand command) throws Exception {
        Long deviceId = command.getDevice().getDevice_id();
        Integer speed = command.getSpeed();
        FanCommand newCommand = commandService.handleCreateFanCommand(deviceId, speed);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }

    @PostMapping("/door")
    public ResponseEntity<DoorCommand> sendDoorCommand(@RequestBody DoorCommand command) throws Exception {
        Long deviceId = command.getDevice().getDevice_id();
        String status = command.getStatus();
        DoorCommand newCommand = commandService.handleCreateDoorCommand(deviceId, status);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }

    @PostMapping("/light")
    public ResponseEntity<LightCommand> sendCommand(@RequestBody LightCommand command) throws Exception {
        Long deviceId = command.getDevice().getDevice_id();
        Color color = command.getColor();
        String status = command.getStatus();
        LightCommand newCommand = commandService.handleCreateLightCommand(deviceId, color, status);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }
}