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
    @Column(columnDefinition = "INT", nullable = false)
    private Integer speed;
}
