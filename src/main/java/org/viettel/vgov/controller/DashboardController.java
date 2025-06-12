package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viettel.vgov.dto.response.DashboardResponseDto;
import org.viettel.vgov.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Analytics", description = "Dashboard overview and summary data")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @Operation(summary = "Get dashboard overview", description = "Retrieve dashboard overview with key metrics and summaries")
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEV') or hasRole('BA') or hasRole('TEST')")
    public ResponseEntity<DashboardResponseDto> getDashboardOverview() {
        DashboardResponseDto dashboard = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboard);
    }
}
