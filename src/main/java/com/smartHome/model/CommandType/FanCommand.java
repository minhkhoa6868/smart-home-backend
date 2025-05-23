package com.smartHome.model.CommandType;

import com.smartHome.model.Command;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fan_commands")
@Getter
@Setter
@DiscriminatorValue("FANCOMMAND")
public class FanCommand extends Command {
    @Column(columnDefinition = "VARCHAR(1)", nullable = false)
    private String speed;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String status;
}
