package org.viettel.vgov.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectMemberResponseDto {
    
    private Long id;
    
    private Long projectId;
    
    private String projectName;
    
    private Long userId;
    
    private String userFullName;
    
    private String userEmail;
    
    private String userRole;
    
    private BigDecimal workloadPercentage;
    
    private LocalDate joinedDate;
    
    private LocalDate leftDate;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
}
