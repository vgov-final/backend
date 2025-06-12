package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viettel.vgov.dto.request.UserRequestDto;
import org.viettel.vgov.dto.response.PagedResponse;
import org.viettel.vgov.dto.response.PmInfoResponseDto;
import org.viettel.vgov.dto.response.StandardResponse;
import org.viettel.vgov.dto.response.UserResponseDto;
import org.viettel.vgov.model.User;
import org.viettel.vgov.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User CRUD operations (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "Get all users", description = "Retrieve paginated list of all users with filters (Admin and PM)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PM')")
    public ResponseEntity<StandardResponse<PagedResponse<UserResponseDto>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive) {
        PagedResponse<UserResponseDto> users = userService.getAllUsers(pageable, search, role, isActive);
        return ResponseEntity.ok(StandardResponse.success(users));
    }
    
    @Operation(summary = "Get available user roles", description = "Get list of available user roles (Admin only)")
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<Map<String, String>[]>> getUserRoles() {
        Map<String, String>[] roles = userService.getAvailableRoles();
        return ResponseEntity.ok(StandardResponse.success(roles));
    }
    
    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID (Admin only)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(StandardResponse.success(user));
    }
    
    @Operation(summary = "Create new user", description = "Create a new user account (Admin only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto user = userService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success(user, "User created successfully"));
    }
    
    @Operation(summary = "Update user", description = "Update user information (Admin only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto user = userService.updateUser(id, requestDto);
        return ResponseEntity.ok(StandardResponse.success(user, "User updated successfully"));
    }
    
    @Operation(summary = "Deactivate user", description = "Deactivate user account (soft delete, Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<String>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(StandardResponse.success("User deactivated successfully"));
    }
    
    @Operation(summary = "Change user role", description = "Update user's role (Admin only). Note: Admin role cannot be changed")
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<UserResponseDto>> changeUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        User.Role newRole = User.Role.valueOf(request.get("role"));
        UserResponseDto user = userService.changeUserRole(id, newRole);
        return ResponseEntity.ok(StandardResponse.success(user, "User role updated successfully"));
    }
    
    @Operation(summary = "Activate/Deactivate user", description = "Toggle user active status (Admin only)")
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<UserResponseDto>> activateDeactivateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        boolean isActive = request.get("isActive");
        UserResponseDto user = userService.activateDeactivateUser(id, isActive);
        String message = isActive ? "User activated successfully" : "User deactivated successfully";
        return ResponseEntity.ok(StandardResponse.success(user, message));
    }
    
    @Operation(summary = "Get user workload", description = "Get user's current workload across all projects (Admin only)")
    @GetMapping("/{id}/workload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<Map<String, Object>>> getUserWorkload(@PathVariable Long id) {
        Map<String, Object> workload = userService.getUserWorkload(id);
        return ResponseEntity.ok(StandardResponse.success(workload));
    }
    
    @Operation(summary = "Get all PM information", description = "Get list of all project managers with their active project counts and total workloads (Admin only)")
    @GetMapping("/pms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<List<PmInfoResponseDto>>> getAllPMs() {
        List<PmInfoResponseDto> pms = userService.getAllPMsInfo();
        return ResponseEntity.ok(StandardResponse.success(pms));
    }
    
}
