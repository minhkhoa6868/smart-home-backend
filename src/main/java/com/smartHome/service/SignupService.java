package com.smartHome.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartHome.dto.UserDTO;
import com.smartHome.model.User;
import com.smartHome.repository.UserRepository;

@Service
public class SignupService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // handle create account
    public UserDTO handleCreateAccount(UserDTO userDTO) {
        // check if user already exists
        userRepository.findByUsername(userDTO.getUsername())
            .ifPresent(user -> {
                throw new RuntimeException("User already exists");
            });
            
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);

        return new UserDTO(user);
    }
}
