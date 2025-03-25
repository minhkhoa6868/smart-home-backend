package com.smartHome.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.dto.FanCommandDTO;
import com.smartHome.dto.LightColorCommandDTO;
import com.smartHome.dto.LightStatusCommandDTO;
import com.smartHome.model.CommandType.DoorCommand;
import com.smartHome.model.CommandType.FanCommand;
import com.smartHome.model.CommandType.LightCommand;
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
    public ResponseEntity<FanCommandDTO> sendFanCommand(@RequestBody FanCommand command) throws Exception {
        FanCommandDTO newCommand = commandService.handleCreateFanCommand(command.getSpeed(), "itsmejoanro/feeds/bbc-fan");

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }

    @PostMapping("/door")
    public ResponseEntity<DoorCommand> sendDoorCommand(@RequestBody DoorCommand command) throws Exception {
        String deviceId = command.getDevice().getDeviceId();
        String status = command.getStatus();
        DoorCommand newCommand = commandService.handleCreateDoorCommand(deviceId, status, "itsmejoanro/feeds/bbc-door");

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }

    @PostMapping("/light/color")
    public ResponseEntity<LightColorCommandDTO> sendLightColorCommand(@RequestBody LightCommand command) throws Exception {
        LightColorCommandDTO newCommand = commandService.handleCreateLightColorCommand(command.getColor(), "itsmejoanro/feeds/bbc-led");

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }

    @PostMapping("/light/status")
    public ResponseEntity<LightStatusCommandDTO> sendLightStatusCommand(@RequestBody LightCommand command) throws Exception {
        LightStatusCommandDTO newCommand = commandService.handleCreateLightStatusCommand(command.getStatus(), "itsmejoanro/feeds/bbc-led");

        return ResponseEntity.status(HttpStatus.CREATED).body(newCommand);
    }
}