package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viettel.vgov.model.Project;
import org.viettel.vgov.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "System & Lookup", description = "System information and lookup data endpoints")
public class SystemController {

    @Operation(summary = "System health check", description = "Check system health and status (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", String.valueOf(System.currentTimeMillis()),
                "application", "V-GOV Backend"
        ));
    }

    @Operation(summary = "Get system version", description = "Retrieve system version and build information")
    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> getVersion() {
        return ResponseEntity.ok(Map.of(
                "version", "1.0.0",
                "build", "2024-01-01",
                "environment", "development"
        ));
    }
}

@RestController
@RequestMapping("/api/lookup")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "System & Lookup", description = "Lookup data for forms and dropdowns")
class LookupController {

    @Operation(summary = "Get available user roles", description = "Retrieve list of all available user roles")
    @GetMapping("/roles")
    public ResponseEntity<List<User.Role>> getRoles() {
        return ResponseEntity.ok(Arrays.asList(User.Role.values()));
    }

    @Operation(summary = "Get project types", description = "Retrieve list of all available project types")
    @GetMapping("/project-types")
    public ResponseEntity<List<Project.ProjectType>> getProjectTypes() {
        return ResponseEntity.ok(Arrays.asList(Project.ProjectType.values()));
    }

    @Operation(summary = "Get project statuses", description = "Retrieve list of all available project statuses")
    @GetMapping("/project-statuses")
    public ResponseEntity<List<Project.Status>> getProjectStatuses() {
        return ResponseEntity.ok(Arrays.asList(Project.Status.values()));
    }
}
