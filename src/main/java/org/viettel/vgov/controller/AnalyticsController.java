package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viettel.vgov.dto.response.AnalyticsResponseDto;
import org.viettel.vgov.service.AnalyticsService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Analytics", description = "Analytics data and reporting endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @Operation(summary = "Get project analytics", description = "Retrieve project analytics data (Admin/PM only)")
    @GetMapping("/projects")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PM')")
    public ResponseEntity<AnalyticsResponseDto> getProjectAnalytics() {
        AnalyticsResponseDto analytics = analyticsService.getProjectAnalytics();
        return ResponseEntity.ok(analytics);
    }
    
    @Operation(summary = "Get employee analytics", description = "Retrieve employee performance analytics (Admin only)")
    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsResponseDto> getEmployeeAnalytics() {
        AnalyticsResponseDto analytics = analyticsService.getEmployeeAnalytics();
        return ResponseEntity.ok(analytics);
    }
    
    @Operation(summary = "Get workload analytics", description = "Retrieve team workload analytics (Admin only)")
    @GetMapping("/workload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsResponseDto> getWorkloadAnalytics() {
        AnalyticsResponseDto analytics = analyticsService.getWorkloadAnalytics();
        return ResponseEntity.ok(analytics);
    }
    
    @Operation(summary = "Get project timeline", description = "Retrieve project timeline and milestones")
    @GetMapping("/project/{id}/timeline")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PM') or @projectSecurityService.canAccessProject(#id, authentication.name)")
    public ResponseEntity<AnalyticsResponseDto> getProjectTimeline(@PathVariable Long id) {
        AnalyticsResponseDto timeline = analyticsService.getProjectTimeline(id);
        return ResponseEntity.ok(timeline);
    }
    
    @Operation(summary = "Get project timeline analytics", description = "Retrieve project timeline analytics data for charts")
    @GetMapping("/project-timeline")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PM') or hasRole('USER')")
    public ResponseEntity<AnalyticsResponseDto> getProjectTimelineAnalytics(
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer startMonth,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) Integer endMonth) {
        
        // Set default date range if not provided (past 12 months)
        LocalDate endDate;
        LocalDate startDate;
        
        if (endYear != null && endMonth != null) {
            endDate = LocalDate.of(endYear, endMonth, 1).plusMonths(1).minusDays(1);
        } else {
            endDate = LocalDate.now();
        }
        
        if (startYear != null && startMonth != null) {
            startDate = LocalDate.of(startYear, startMonth, 1);
        } else {
            startDate = endDate.minusMonths(12).withDayOfMonth(1);
        }
        
        AnalyticsResponseDto analytics = analyticsService.getProjectTimelineAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }
}
