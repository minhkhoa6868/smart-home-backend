package com.smartHome.model;

import java.util.Set;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@Setter
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

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
    private Set<Device> hasDevices;
}
