package org.viettel.vgov.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDto {
    
    private Long id;
    
    private String title;
    
    private String message;
    
    private String notificationType;
    
    private Long relatedProjectId;
    
    private String relatedProjectName;
    
    private Long relatedUserId;
    
    private String relatedUserName;
    
    private Boolean isRead;
    
    private LocalDateTime createdAt;
}
