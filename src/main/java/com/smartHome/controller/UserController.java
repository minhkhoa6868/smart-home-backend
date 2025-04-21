package com.smartHome.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.dto.DeviceDTO;
import com.smartHome.dto.MemberDTO;
import com.smartHome.dto.UserDTO;
import com.smartHome.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // get all members except recent user
    @GetMapping("/{userId}/members")
    public ResponseEntity<List<MemberDTO>> getAllMembers(@PathVariable Long userId) {
        List<MemberDTO> members = userService.handleGetAllMembers(userId);

        return ResponseEntity.ok(members);
    }

    // get user
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        UserDTO user = userService.handleGetUser(userId);

        return ResponseEntity.ok(user);
    }

    // get all devices of user
    @GetMapping("/{userId}/devices")
    public ResponseEntity<List<DeviceDTO>> getUserDevices(@PathVariable Long userId) {
        List<DeviceDTO> devices = userService.handleGetUserDevices(userId);

        return ResponseEntity.ok(devices);
    }

    // update user device
    @PostMapping("/{userId}/devices/{deviceId}")
    public ResponseEntity<UserDTO> updateUserDevices(@PathVariable Long userId, @PathVariable String deviceId) {
        UserDTO updateUser = userService.handleUpdateUserDevices(userId, deviceId);
        
        return ResponseEntity.ok(updateUser);
    }
}
