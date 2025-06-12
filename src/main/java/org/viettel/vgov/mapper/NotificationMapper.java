package org.viettel.vgov.mapper;

import org.springframework.stereotype.Component;
import org.viettel.vgov.dto.response.NotificationResponseDto;
import org.viettel.vgov.model.Notification;

@Component
public class NotificationMapper {
    
    public NotificationResponseDto toResponseDto(Notification entity) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setNotificationType(entity.getNotificationType());
        dto.setIsRead(entity.getIsRead());
        dto.setCreatedAt(entity.getCreatedAt());
        
        if (entity.getRelatedProject() != null) {
            dto.setRelatedProjectId(entity.getRelatedProject().getId());
            dto.setRelatedProjectName(entity.getRelatedProject().getProjectName());
        }
        
        if (entity.getRelatedUser() != null) {
            dto.setRelatedUserId(entity.getRelatedUser().getId());
            dto.setRelatedUserName(entity.getRelatedUser().getFullName());
        }
        
        return dto;
    }
}
