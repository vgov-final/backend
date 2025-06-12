package org.viettel.vgov.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WorkLogRequestDto {
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    // Optional field: If provided, PM can create work log for this user (only PM can do this)
    // If null, work log is created for the current user
    private Long userId;
    
    @NotNull(message = "Work date is required")
    private LocalDate workDate;
    
    @NotNull(message = "Hours worked is required")
    @DecimalMin(value = "0.01", message = "Hours worked must be greater than 0")
    @DecimalMax(value = "24.00", message = "Hours worked cannot exceed 24 hours")
    private BigDecimal hoursWorked;
    
    private String taskFeature;
    
    @NotBlank(message = "Work description is required")
    private String workDescription;
}
