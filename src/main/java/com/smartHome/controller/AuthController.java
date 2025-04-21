package com.smartHome.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartHome.dto.UserDTO;
import com.smartHome.security.JwtUtil;
import com.smartHome.service.AuthenticationService;
import com.smartHome.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AuthenticationService authenticationService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        String username = user.getUsername();
        String password = user.getPassword();

        try {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid username or password"));
        }
        UserDetails userDetails = authenticationService.loadUserByUsername(username);
        String token = jwtUtil.generateToken(userDetails);

        Long userId = userService.getUserIdByUsername(username);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", userId);

        return ResponseEntity.ok(response);
    }
    
}
