package com.smartHome.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.smartHome.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String password;
    private List<DeviceDTO> devices;

    public UserDTO() {}

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        
        if (user.getHasDevices() != null) {
            this.devices = user.getHasDevices().stream().map(DeviceDTO::new).collect(Collectors.toList());
        }
    }
}
