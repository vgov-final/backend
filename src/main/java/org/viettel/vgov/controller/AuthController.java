package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.viettel.vgov.dto.request.LoginRequestDto;
import org.viettel.vgov.dto.response.JwtResponseDto;
import org.viettel.vgov.dto.response.UserResponseDto;
import org.viettel.vgov.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(summary = "User login", description = "Authenticate user with email and password")
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        JwtResponseDto response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "User logout", description = "Logout current user (invalidate session)")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser() {
        authService.logout();
        return ResponseEntity.ok(Map.of("message", "User logged out successfully"));
    }
    
    @Operation(summary = "Refresh JWT token", description = "Generate new JWT token for user")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newToken = authService.refreshToken(email);
        return ResponseEntity.ok(Map.of("token", newToken, "type", "Bearer"));
    }
    
    @Operation(summary = "Get current user info", description = "Retrieve information about the currently authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        UserResponseDto user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}
