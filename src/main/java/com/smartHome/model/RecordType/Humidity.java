package com.smartHome.model.RecordType;

import com.smartHome.model.Record;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("HUMIDITY")
public class Humidity extends Record {
    @Column(nullable = false)
    private Float temperature;
}
