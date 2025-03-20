package com.smartHome.model.RecordType;

import com.smartHome.model.Record;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "light_records")
@Getter
@Setter
@DiscriminatorValue("LIGHT")
public class Light extends Record {
    @Column(nullable = false)
    private Double brightness;
}
