package com.smartHome.model.RecordType;

import com.smartHome.model.Record;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("DISTANCE")
public class Distance extends Record {
    @Column(nullable = false)
    private Float distance;
}
