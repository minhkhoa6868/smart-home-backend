package com.smartHome.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.dto.UserDTO;
import com.smartHome.service.SignupService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class SignupController {
    @Autowired
    private SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<?> createAccount(@RequestBody UserDTO user) {
        UserDTO newUser = signupService.handleCreateAccount(user);        
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
    
}
