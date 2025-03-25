package com.smartHome.model.RecordType;

import com.smartHome.model.Record;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("LIGHT")
public class Light extends Record {
    @Column(nullable = false)
    private Float brightness;
}
