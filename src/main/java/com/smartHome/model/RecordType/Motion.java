package com.smartHome.model.RecordType;

import com.smartHome.model.Record;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("MOTION")
public class Motion extends Record {
    @Column(nullable = false)
    private Boolean motion;
}
