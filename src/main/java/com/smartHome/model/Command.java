package com.smartHome.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "device")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "command_type")
@Setter
@Getter
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long command_id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @MapsId("deviceId")
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
}
