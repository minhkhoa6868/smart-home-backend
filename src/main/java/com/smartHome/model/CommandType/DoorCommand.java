package com.smartHome.model.CommandType;

import com.smartHome.model.Command;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "door_commands")
@Getter
@Setter
@DiscriminatorValue("DOORCOMMAND")
public class DoorCommand extends Command {
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String status;
}
