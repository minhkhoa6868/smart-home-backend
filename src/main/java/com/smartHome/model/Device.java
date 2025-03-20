package com.smartHome.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "devices")
@Setter
@Getter
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String device_name;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String status;

    @Column(columnDefinition = "NUMERIC(10,2)", nullable = false)
    private Long power;

    @ManyToMany(mappedBy = "hasDevices")
    private Set<User> hasByUsers;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<Command> commands;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<Record> records;
}
