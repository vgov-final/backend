package org.viettel.vgov.mapper;

import org.springframework.stereotype.Component;
import org.viettel.vgov.dto.request.UserRequestDto;
import org.viettel.vgov.dto.response.UserResponseDto;
import org.viettel.vgov.model.User;

@Component
public class UserMapper implements BaseMapper<User, UserRequestDto, UserResponseDto> {
    
    @Override
    public User toEntity(UserRequestDto requestDto) {
        User user = new User();
        user.setEmployeeCode(requestDto.getEmployeeCode());
        user.setFullName(requestDto.getFullName());
        user.setEmail(requestDto.getEmail());
        user.setRole(requestDto.getRole());
        user.setGender(requestDto.getGender());
        user.setBirthDate(requestDto.getBirthDate());
        user.setProfilePhotoUrl(requestDto.getProfilePhotoUrl());
        user.setIsActive(requestDto.getIsActive());
        return user;
    }
    
    @Override
    public UserResponseDto toResponseDto(User entity) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(entity.getId());
        dto.setEmployeeCode(entity.getEmployeeCode());
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setRole(entity.getRole());
        dto.setGender(entity.getGender());
        dto.setBirthDate(entity.getBirthDate());
        dto.setProfilePhotoUrl(entity.getProfilePhotoUrl());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        if (entity.getCreatedBy() != null) {
            dto.setCreatedBy(entity.getCreatedBy().getFullName());
        }
        if (entity.getUpdatedBy() != null) {
            dto.setUpdatedBy(entity.getUpdatedBy().getFullName());
        }
        
        return dto;
    }
    
    public void updateEntityFromDto(UserRequestDto requestDto, User user) {
        user.setEmployeeCode(requestDto.getEmployeeCode());
        user.setFullName(requestDto.getFullName());
        user.setEmail(requestDto.getEmail());
        user.setRole(requestDto.getRole());
        user.setGender(requestDto.getGender());
        user.setBirthDate(requestDto.getBirthDate());
        user.setProfilePhotoUrl(requestDto.getProfilePhotoUrl());
        user.setIsActive(requestDto.getIsActive());
    }
}
