package com.smartHome.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoModeDTO {
    private Boolean isAutoMode;

    public AutoModeDTO() {}

    public AutoModeDTO(Boolean isAutoMode) {
        this.isAutoMode = isAutoMode;
    }
}
