package org.viettel.vgov.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateRequestDto {
    
    private String fullName;
    private String gender;
    private String birthDate;
    private String profilePhotoUrl;
}
