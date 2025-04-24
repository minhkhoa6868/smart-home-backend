package com.smartHome.dto;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeRangeDTO {
    private ZonedDateTime starTime;
    private ZonedDateTime endTime;
}
