package org.viettel.vgov.dto.response;

import lombok.Data;
import org.viettel.vgov.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    
    private Long id;
    
    private String employeeCode;
    
    private String fullName;
    
    private String email;
    
    private User.Role role;
    
    private User.Gender gender;
    
    private LocalDate birthDate;
    
    private String profilePhotoUrl;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
}
