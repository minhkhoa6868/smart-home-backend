package com.smartHome.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.dto.UserSettingDTO;
import com.smartHome.service.UserSettingService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/setting")
public class UserSettingController {
    private final UserSettingService userSettingService;

    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    @PostMapping("/auto-mode")
    public String setAutoMode(@RequestBody UserSettingDTO userSettingDTO) {
        userSettingService.handleAutoMode("LED-1", userSettingDTO.getAutoMode());
        
        return userSettingDTO.getAutoMode() ? "Auto mode is on" : "Auto mode is off";
    }
}
