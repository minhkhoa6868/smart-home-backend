package com.smartHome.dto;

import com.smartHome.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String password;

    public UserDTO() {}

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}
