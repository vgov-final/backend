package org.viettel.vgov.mapper;

import org.springframework.stereotype.Component;
import org.viettel.vgov.dto.request.ProjectMemberRequestDto;
import org.viettel.vgov.dto.response.ProjectMemberResponseDto;
import org.viettel.vgov.model.ProjectMember;

@Component
public class ProjectMemberMapper implements BaseMapper<ProjectMember, ProjectMemberRequestDto, ProjectMemberResponseDto> {
    
    @Override
    public ProjectMember toEntity(ProjectMemberRequestDto requestDto) {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setWorkloadPercentage(requestDto.getWorkloadPercentage());
        projectMember.setJoinedDate(requestDto.getJoinedDate());
        projectMember.setLeftDate(requestDto.getLeftDate());
        projectMember.setIsActive(requestDto.getIsActive());
        return projectMember;
    }
    
    @Override
    public ProjectMemberResponseDto toResponseDto(ProjectMember entity) {
        ProjectMemberResponseDto dto = new ProjectMemberResponseDto();
        dto.setId(entity.getId());
        dto.setWorkloadPercentage(entity.getWorkloadPercentage());
        dto.setJoinedDate(entity.getJoinedDate());
        dto.setLeftDate(entity.getLeftDate());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        if (entity.getProject() != null) {
            dto.setProjectId(entity.getProject().getId());
            dto.setProjectName(entity.getProject().getProjectName());
        }
        
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserFullName(entity.getUser().getFullName());
            dto.setUserEmail(entity.getUser().getEmail());
            dto.setUserRole(entity.getUser().getRole().name());
        }
        
        if (entity.getCreatedBy() != null) {
            dto.setCreatedBy(entity.getCreatedBy().getFullName());
        }
        
        return dto;
    }
}
