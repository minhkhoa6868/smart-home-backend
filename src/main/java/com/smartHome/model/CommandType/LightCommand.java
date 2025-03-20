package com.smartHome.model.CommandType;

import com.smartHome.model.Command;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "light_command")
@Getter
@Setter
@DiscriminatorValue("LIGHTCOMMAND")
public class LightCommand extends Command {
    public enum Color {
        red, blue, green
    }

    @Column(nullable = false)
    private Color color;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String status;
}
