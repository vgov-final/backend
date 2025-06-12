package org.viettel.vgov.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.viettel.vgov.model.Project;

import java.time.LocalDate;

@Data
public class ProjectRequestDto {
    
    @NotBlank(message = "Project code is required")
    private String projectCode;
    
    @NotBlank(message = "Project name is required")
    private String projectName;
    
    @NotBlank(message = "PM email is required")
    @Email(message = "Invalid PM email format")
    private String pmEmail;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(message = "Project type is required")
    private Project.ProjectType projectType;
    
    @NotNull(message = "Status is required")
    private Project.Status status;
    
    private String description;
}
