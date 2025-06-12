package org.viettel.vgov.dto.response;

import lombok.Data;
import org.viettel.vgov.model.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectResponseDto {
    
    private Long id;
    
    private String projectCode;
    
    private String projectName;
    
    private String pmEmail;
    
    private LocalDate startDate;
    
    private LocalDate endDate;

    private LocalDate actualClosedDate;
    
    private Project.ProjectType projectType;
    
    private Project.Status status;
    
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
}
