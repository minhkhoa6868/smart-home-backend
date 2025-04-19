package com.smartHome.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.smartHome.dto.DeviceDTO;
import com.smartHome.dto.MemberDTO;
import com.smartHome.dto.UserDTO;
import com.smartHome.model.Device;
import com.smartHome.model.User;
import com.smartHome.repository.DeviceRepository;
import com.smartHome.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    public UserService(UserRepository userRepository, DeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
    }

    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"))
                        .getUserId();
    }

    // handle get all members except recent user
    public List<MemberDTO> handleGetAllMembers(Long userId) {
        return userRepository.findAll()
            .stream()
            .filter(user -> !user.getUserId().equals(userId))
            .map(MemberDTO::new)
            .collect(Collectors.toList());
    }

    // handle get one user
    public UserDTO handleGetUser(Long userId) {
        return userRepository.findById(userId)
            .map(UserDTO::new)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // handle get all devices of user
    public List<DeviceDTO> handleGetUserDevices(Long userId) {
        return userRepository.findById(userId)
            .map(user -> user.getHasDevices().stream()
                            .sorted(Comparator.comparing(Device::getDeviceId))
                            .map(DeviceDTO::new)
                            .collect(Collectors.toList()))
            .orElse(List.of());       
    }

    // handle update devices of user
    public UserDTO handleUpdateUserDevices(Long userId, String deviceId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Device device = deviceRepository.findByDeviceId(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));

        user.getHasDevices().add(device);

        device.getHasByUsers().add(user);

        userRepository.save(user);
        deviceRepository.save(device);

        return new UserDTO(user);
    }
}
