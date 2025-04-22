package com.smartHome.model;

import java.time.ZonedDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "commands")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "command_type")
@Setter
@Getter
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long command_id;

    @Column(nullable = false)
    private ZonedDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
