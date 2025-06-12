package org.viettel.vgov.mapper;

import org.springframework.stereotype.Component;
import org.viettel.vgov.dto.request.WorkLogRequestDto;
import org.viettel.vgov.dto.response.WorkLogResponseDto;
import org.viettel.vgov.model.WorkLog;

@Component
public class WorkLogMapper implements BaseMapper<WorkLog, WorkLogRequestDto, WorkLogResponseDto> {
    
    @Override
    public WorkLog toEntity(WorkLogRequestDto requestDto) {
        WorkLog workLog = new WorkLog();
        workLog.setWorkDate(requestDto.getWorkDate());
        workLog.setHoursWorked(requestDto.getHoursWorked());
        workLog.setTaskFeature(requestDto.getTaskFeature());
        workLog.setWorkDescription(requestDto.getWorkDescription());
        return workLog;
    }
    
    @Override
    public WorkLogResponseDto toResponseDto(WorkLog entity) {
        WorkLogResponseDto dto = new WorkLogResponseDto();
        dto.setId(entity.getId());
        dto.setWorkDate(entity.getWorkDate());
        dto.setHoursWorked(entity.getHoursWorked());
        dto.setTaskFeature(entity.getTaskFeature());
        dto.setWorkDescription(entity.getWorkDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserFullName(entity.getUser().getFullName());
        }
        
        if (entity.getProject() != null) {
            dto.setProjectId(entity.getProject().getId());
            dto.setProjectName(entity.getProject().getProjectName());
        }
        
        return dto;
    }
}
