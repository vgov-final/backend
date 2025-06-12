package org.viettel.vgov.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDto {
    
    private String token;
    
    private String type = "Bearer";
    
    private UserResponseDto user;
    
    public JwtResponseDto(String accessToken, UserResponseDto user) {
        this.token = accessToken;
        this.user = user;
    }
}
