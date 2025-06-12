package org.viettel.vgov.mapper;

import org.springframework.stereotype.Component;
import org.viettel.vgov.dto.request.ProjectRequestDto;
import org.viettel.vgov.dto.response.ProjectResponseDto;
import org.viettel.vgov.model.Project;

@Component
public class ProjectMapper implements BaseMapper<Project, ProjectRequestDto, ProjectResponseDto> {
    
    @Override
    public Project toEntity(ProjectRequestDto requestDto) {
        Project project = new Project();
        project.setProjectCode(requestDto.getProjectCode());
        project.setProjectName(requestDto.getProjectName());
        project.setPmEmail(requestDto.getPmEmail());
        project.setStartDate(requestDto.getStartDate());
        project.setEndDate(requestDto.getEndDate());
        project.setProjectType(requestDto.getProjectType());
        project.setStatus(requestDto.getStatus());
        project.setDescription(requestDto.getDescription());
        return project;
    }
    
    @Override
    public ProjectResponseDto toResponseDto(Project entity) {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.setId(entity.getId());
        dto.setProjectCode(entity.getProjectCode());
        dto.setProjectName(entity.getProjectName());
        dto.setPmEmail(entity.getPmEmail());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setActualClosedDate(entity.getActualClosedDate());
        dto.setProjectType(entity.getProjectType());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        if (entity.getCreatedBy() != null) {
            dto.setCreatedBy(entity.getCreatedBy().getFullName());
        }
        
        if (entity.getUpdatedBy() != null) {
            dto.setUpdatedBy(entity.getUpdatedBy().getFullName());
        }
        
        return dto;
    }
}
