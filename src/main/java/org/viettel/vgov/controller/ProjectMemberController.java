package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viettel.vgov.dto.request.ProjectMemberRequestDto;
import org.viettel.vgov.dto.response.ProjectMemberResponseDto;
import org.viettel.vgov.service.ProjectMemberService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Project Members", description = "Project member assignment and workload management")
@SecurityRequirement(name = "bearerAuth")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @Operation(summary = "Get project members", description = "Get all members assigned to a project")
    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMemberResponseDto>> getProjectMembers(@PathVariable Long id) {
        List<ProjectMemberResponseDto> members = projectMemberService.getProjectMembers(id);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "Add member to project", description = "Assign user to project with workload allocation (Admin only)")
    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectMemberResponseDto> addMemberToProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectMemberRequestDto requestDto) {
        ProjectMemberResponseDto member = projectMemberService.addMemberToProject(id, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @Operation(summary = "Update member workload", description = "Update workload allocation for project member (Admin only)")
    @PutMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectMemberResponseDto> updateMemberWorkload(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody ProjectMemberRequestDto requestDto) {
        ProjectMemberResponseDto member = projectMemberService.updateMemberWorkload(id, userId, requestDto);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "Remove member from project", description = "Remove user from project assignment (Admin only)")
    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> removeMemberFromProject(
            @PathVariable Long id,
            @PathVariable Long userId) {
        projectMemberService.removeMemberFromProject(id, userId);
        return ResponseEntity.ok(Map.of("message", "Member removed from project successfully"));
    }
}
