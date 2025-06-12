package org.viettel.vgov.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkLogResponseDto {
    
    private Long id;
    
    private Long userId;
    
    private String userFullName;
    
    private Long projectId;
    
    private String projectName;
    
    private LocalDate workDate;
    
    private BigDecimal hoursWorked;
    
    private String taskFeature;
    
    private String workDescription;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
