package com.smartHome.model.RecordType;

import com.smartHome.model.Record;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("TEMPERATURE")
public class Temperature extends Record {
    @Column(nullable = false)
    private Float temperature;
}
