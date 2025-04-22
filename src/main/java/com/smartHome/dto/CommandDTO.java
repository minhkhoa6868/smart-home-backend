package com.smartHome.dto;

import java.time.ZonedDateTime;

import com.smartHome.model.Command;
import com.smartHome.model.CommandType.DoorCommand;
import com.smartHome.model.CommandType.FanCommand;
import com.smartHome.model.CommandType.LightCommand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandDTO {
    private ZonedDateTime timestamp;
    private String deviceName;
    private String status;
    private String userName;

    public CommandDTO(){}

    public CommandDTO(Command command) {
        this.timestamp = command.getTimestamp();
        this.deviceName = command.getDevice().getDevice_name();

        if (command.getUser() != null) {
            this.userName = command.getUser().getUsername();
        }

        if (command instanceof FanCommand) {
            this.status = ((FanCommand) command).getStatus();
        }

        else if (command instanceof LightCommand) {
            this.status = ((LightCommand) command).getStatus();
        }

        else if (command instanceof DoorCommand) {
            this.status = ((DoorCommand) command).getStatus();
        }
    }
}
