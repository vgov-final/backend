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
import org.viettel.vgov.dto.request.ProjectRequestDto;
import org.viettel.vgov.dto.response.PagedResponse;
import org.viettel.vgov.dto.response.ProjectResponseDto;
import org.viettel.vgov.dto.response.StandardResponse;
import org.viettel.vgov.model.Project;
import org.viettel.vgov.service.ProjectService;

import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "Project CRUD operations and status management")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {
    
    private final ProjectService projectService;
    
    @Operation(summary = "Get all projects", description = "List projects based on role permissions with filters - Admin: all projects, PM: managed projects, Others: assigned projects")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PM') or hasRole('DEV') or hasRole('BA') or hasRole('TEST')")
    public ResponseEntity<StandardResponse<PagedResponse<ProjectResponseDto>>> getAllProjects(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String projectStatus,
            @RequestParam(required = false) String projectType) {
        PagedResponse<ProjectResponseDto> projects = projectService.getAllProjects(pageable, search, projectStatus, projectType);
        return ResponseEntity.ok(StandardResponse.success(projects));
    }
    
    @Operation(summary = "Get project by ID", description = "Get project details by ID - role-based access control")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PM') or @projectSecurityService.canAccessProject(#id, authentication.name)")
    public ResponseEntity<StandardResponse<ProjectResponseDto>> getProjectById(@PathVariable Long id) {
        ProjectResponseDto project = projectService.getProjectById(id);
        return ResponseEntity.ok(StandardResponse.success(project));
    }
    
    @Operation(summary = "Create new project", description = "Create a new project (Admin only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<ProjectResponseDto>> createProject(@Valid @RequestBody ProjectRequestDto requestDto) {
        ProjectResponseDto project = projectService.createProject(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success(project, "Project created successfully"));
    }
    
    @Operation(summary = "Update project", description = "Update project information (Admin only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<ProjectResponseDto>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDto requestDto) {
        ProjectResponseDto project = projectService.updateProject(id, requestDto);
        return ResponseEntity.ok(StandardResponse.success(project, "Proj ect updated successfully"));
    }
    
    @Operation(summary = "Delete project", description = "Delete project (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<String>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(StandardResponse.success("Project deleted successfully"));
    }
    
    @Operation(summary = "Update project status", description = "Update project status (Admin only)")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<ProjectResponseDto>> updateProjectStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Project.Status status = Project.Status.valueOf(request.get("status"));
        ProjectResponseDto project = projectService.updateProjectStatus(id, status);
        return ResponseEntity.ok(StandardResponse.success(project, "Project status updated successfully"));
    }
}
