package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.viettel.vgov.dto.request.PasswordChangeRequestDto;
import org.viettel.vgov.dto.request.ProfileUpdateRequestDto;
import org.viettel.vgov.dto.response.UserResponseDto;
import org.viettel.vgov.service.ProfileService;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "User profile and personal settings")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {
    
    private final ProfileService profileService;
    
    @Operation(summary = "Get current user profile", description = "Retrieve current user's profile information")
    @GetMapping
    public ResponseEntity<UserResponseDto> getCurrentProfile() {
        UserResponseDto profile = profileService.getCurrentProfile();
        return ResponseEntity.ok(profile);
    }
    
    @Operation(summary = "Update user profile", description = "Update user's profile information")
    @PutMapping
    public ResponseEntity<UserResponseDto> updateProfile(@Valid @RequestBody ProfileUpdateRequestDto requestDto) {
        UserResponseDto profile = profileService.updateProfile(requestDto);
        return ResponseEntity.ok(profile);
    }
    
    @Operation(summary = "Change password", description = "Change user's password")
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody PasswordChangeRequestDto requestDto) {
        profileService.changePassword(requestDto);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
