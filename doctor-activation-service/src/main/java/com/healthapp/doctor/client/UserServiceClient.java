package com.healthapp.doctor.client;

import com.healthapp.shared.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign Client to communicate with User Service
 */
@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    /**
     * Get user by ID
     */
    @GetMapping("/api/v1/users/{userId}")
    UserDto getUserById(@PathVariable String userId);

    /**
     * Get multiple users by IDs (batch fetch)
     */
    @PostMapping("/api/v1/users/batch")
    List<UserDto> getUsersByIds(@RequestBody List<String> userIds);
}