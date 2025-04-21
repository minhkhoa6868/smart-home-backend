package com.smartHome.dto;

import com.smartHome.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
    private String name;

    public MemberDTO() {}

    public MemberDTO(User user) {
        this.name = user.getUsername();
    }
}
