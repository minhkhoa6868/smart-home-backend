package com.smartHome.model;

import java.util.List;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    private String username;
    
    @Column(columnDefinition = "VARCHAR(60)", nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_has_devices",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "device_id")
    )
    private List<Device> hasDevices;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Command> commands;
}