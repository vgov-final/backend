package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viettel.vgov.dto.request.WorkLogRequestDto;
import org.viettel.vgov.dto.response.WorkLogResponseDto;
import org.viettel.vgov.service.WorkLogService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/worklogs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Work Logs", description = "Work log tracking and reporting")
@SecurityRequirement(name = "bearerAuth")
public class WorkLogController {
    
    private final WorkLogService workLogService;
    
    @Operation(summary = "Get all work logs", description = "Get all work logs with role-based filtering and search")
    @GetMapping
    public ResponseEntity<List<WorkLogResponseDto>> getAllWorkLogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String workDateFrom,
            @RequestParam(required = false) String workDateTo,
            @RequestParam(required = false) Double minHours,
            @RequestParam(required = false) Double maxHours,
            @RequestParam(required = false) String taskFeature,
            @RequestParam(required = false, defaultValue = "workDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        List<WorkLogResponseDto> workLogs = workLogService.getAllWorkLogs(
            search, projectId, userId, workDateFrom, workDateTo,
            minHours, maxHours, taskFeature, sortBy, sortDir);
        return ResponseEntity.ok(workLogs);
    }
    
    @Operation(summary = "Get user work logs", description = "Get work logs for specific user (Admin/PM or own logs)")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PM') or @workLogService.canAccessUserWorkLogs(#userId, authentication.name)")
    public ResponseEntity<List<WorkLogResponseDto>> getWorkLogsByUserId(@PathVariable Long userId) {
        List<WorkLogResponseDto> workLogs = workLogService.getWorkLogsByUserId(userId);
        return ResponseEntity.ok(workLogs);
    }
    
    @Operation(summary = "Get project work logs", description = "Get all work logs for specific project")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<WorkLogResponseDto>> getWorkLogsByProjectId(@PathVariable Long projectId) {
        List<WorkLogResponseDto> workLogs = workLogService.getWorkLogsByProjectId(projectId);
        return ResponseEntity.ok(workLogs);
    }
    
    @Operation(summary = "Create work log", description = "Log work hours for project tasks")
    @PostMapping
    @PreAuthorize("hasRole('PM') or hasRole('DEV') or hasRole('BA') or hasRole('TEST')")
    public ResponseEntity<WorkLogResponseDto> createWorkLog(@Valid @RequestBody WorkLogRequestDto requestDto) {
        WorkLogResponseDto workLog = workLogService.createWorkLog(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(workLog);
    }
    
    @Operation(summary = "Update work log", description = "Update existing work log entry")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('DEV') or hasRole('BA') or hasRole('TEST')")
    public ResponseEntity<WorkLogResponseDto> updateWorkLog(
            @PathVariable Long id,
            @Valid @RequestBody WorkLogRequestDto requestDto) {
        WorkLogResponseDto workLog = workLogService.updateWorkLog(id, requestDto);
        return ResponseEntity.ok(workLog);
    }
    
    @Operation(summary = "Delete work log", description = "Delete work log entry")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('DEV') or hasRole('BA') or hasRole('TEST')")
    public ResponseEntity<Map<String, String>> deleteWorkLog(@PathVariable Long id) {
        workLogService.deleteWorkLog(id);
        return ResponseEntity.ok(Map.of("message", "Work log deleted successfully"));
    }
}
