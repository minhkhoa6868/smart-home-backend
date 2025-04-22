package com.smartHome.model;

import java.time.ZonedDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "records")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "record_type")
@Setter
@Getter
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(nullable = false)
    private ZonedDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
}
