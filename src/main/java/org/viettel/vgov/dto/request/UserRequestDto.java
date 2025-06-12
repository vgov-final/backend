package org.viettel.vgov.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.viettel.vgov.model.User;

import java.time.LocalDate;

@Data
public class UserRequestDto {
    
    @NotBlank(message = "Employee code is required")
    private String employeeCode;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotNull(message = "Role is required")
    private User.Role role;
    
    private User.Gender gender;
    
    private LocalDate birthDate;
    
    private Boolean isActive = true;
}
