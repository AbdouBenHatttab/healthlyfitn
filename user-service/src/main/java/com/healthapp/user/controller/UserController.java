package com.healthapp.user.controller;

import com.healthapp.user.dto.request.UpdateUserRequest;
import com.healthapp.user.dto.request.ChangePasswordRequest;
import com.healthapp.user.dto.response.ApiResponse;
import com.healthapp.user.dto.response.UserResponse;
import com.healthapp.user.security.CustomUserPrincipal;
import com.healthapp.user.service.UserService;
import com.healthapp.user.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
@Slf4j
public class UserController {
    
    private final UserService userService;
    private final PasswordService passwordService;
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile(Authentication auth) {
        log.info("Getting current user profile");
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        UserResponse user = userService.getUserById(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", user));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication auth) {
        
        log.info("Updating user profile");
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        UserResponse updated = userService.updateUser(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", updated));
    }
    
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication auth) {
        
        log.info("Change password request");
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        passwordService.changePassword(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed", null));
    }
    
    // @GetMapping("/{userId}")
    // public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId) {
    //     log.info("Getting user: {}", userId);
    //     UserResponse user = userService.getUserById(userId);
    //     return ResponseEntity.ok(ApiResponse.success(user));
    // }

    /**
     * Get multiple users by IDs (batch fetch)
     * Used by doctor-service to fetch patient details
     */
    @PostMapping("/batch")
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<String> userIds) {
        log.info("📦 Batch fetching {} users", userIds.size());

        List<UserDto> users = userService.getUsersByIds(userIds);

        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID (allow access from other services)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        log.info("Getting user: {}", userId);

        UserDto user = userService.getUserById(userId);

        return ResponseEntity.ok(user);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        // Allow access from other services (no @PreAuthorize)
    }

    @PostMapping("/batch")
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<String> userIds) {
        // Allow access from other services (no @PreAuthorize)
    }
}
