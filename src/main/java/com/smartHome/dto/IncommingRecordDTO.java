package com.smartHome.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncommingRecordDTO {
    private String recordType;
    private Double humidity;
    private Double temperature;
    private Double brightness;
    private Boolean motion;
    private Long deviceId;
    private LocalDateTime timestamp;
}
