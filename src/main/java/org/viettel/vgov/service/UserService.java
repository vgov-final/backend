package org.viettel.vgov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viettel.vgov.dto.request.UserRequestDto;
import org.viettel.vgov.dto.response.PagedResponse;
import org.viettel.vgov.dto.response.PmInfoResponseDto;
import org.viettel.vgov.dto.response.UserResponseDto;
import org.viettel.vgov.mapper.UserMapper;
import org.viettel.vgov.model.User;
import org.viettel.vgov.repository.UserRepository;
import org.viettel.vgov.repository.ProjectMemberRepository;
import org.viettel.vgov.security.UserPrincipal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public PagedResponse<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findByIsActiveTrue(pageable);
        Page<UserResponseDto> userDtos = users.map(userMapper::toResponseDto);
        return PagedResponse.of(userDtos);
    }
    
    public PagedResponse<UserResponseDto> getAllUsers(Pageable pageable, String search, String role, Boolean isActive) {
        User.Role roleEnum = null;
        if (role != null && !role.isEmpty()) {
            try {
                roleEnum = User.Role.valueOf(role);
            } catch (IllegalArgumentException e) {
                // Invalid role, keep as null
            }
        }
        
        Page<User> users = userRepository.findUsersWithFilters(search, roleEnum, isActive, pageable);
        Page<UserResponseDto> userDtos = users.map(userMapper::toResponseDto);
        return PagedResponse.of(userDtos);
    }
    
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }
    
    public UserResponseDto createUser(UserRequestDto requestDto) {
        // Check if email already exists
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Check if employee code already exists
        if (userRepository.existsByEmployeeCode(requestDto.getEmployeeCode())) {
            throw new RuntimeException("Employee code is already in use!");
        }
        
        User user = userMapper.toEntity(requestDto);
        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        
        // Set created by current user
        User currentUser = getCurrentUser();
        user.setCreatedBy(currentUser);
        
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }
    
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check if email already exists for other users
        if (!user.getEmail().equals(requestDto.getEmail()) && 
            userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Check if employee code already exists for other users
        if (!user.getEmployeeCode().equals(requestDto.getEmployeeCode()) && 
            userRepository.existsByEmployeeCode(requestDto.getEmployeeCode())) {
            throw new RuntimeException("Employee code is already in use!");
        }
        
        userMapper.updateEntityFromDto(requestDto, user);
        
        // Update password if provided
        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        }
        
        // Set updated by current user
        User currentUser = getCurrentUser();
        user.setUpdatedBy(currentUser);
        
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }
    
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsActive(false);
        User currentUser = getCurrentUser();
        user.setUpdatedBy(currentUser);
        
        userRepository.save(user);
    }
    
    public UserResponseDto changeUserRole(Long id, User.Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Validate role change (admin role cannot be changed)
        if (user.getRole() == User.Role.admin || newRole == User.Role.admin) {
            throw new org.viettel.vgov.exception.InvalidRoleChangeException("Admin role cannot be changed");
        }
        
        user.setRole(newRole);
        User currentUser = getCurrentUser();
        user.setUpdatedBy(currentUser);
        
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }
    
    public UserResponseDto activateDeactivateUser(Long id, boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsActive(isActive);
        User currentUser = getCurrentUser();
        user.setUpdatedBy(currentUser);
        
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }
    
    public Map<String, Object> getUserWorkload(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        Map<String, Object> workloadInfo = new HashMap<>();
        
        // Get current total workload
        BigDecimal totalWorkload = projectMemberRepository.getTotalWorkloadByUserId(id);
        if (totalWorkload == null) {
            totalWorkload = BigDecimal.ZERO;
        }
        
        // Get active project count
        Integer activeProjectCount = projectMemberRepository.countActiveProjectsByUserId(id);
        if (activeProjectCount == null) {
            activeProjectCount = 0;
        }
        
        // Calculate available capacity
        BigDecimal availableCapacity = BigDecimal.valueOf(100).subtract(totalWorkload);
        
        workloadInfo.put("userId", user.getId());
        workloadInfo.put("userName", user.getFullName());
        workloadInfo.put("email", user.getEmail());
        workloadInfo.put("role", user.getRole().name());
        workloadInfo.put("totalWorkload", totalWorkload);
        workloadInfo.put("availableCapacity", availableCapacity);
        workloadInfo.put("activeProjectCount", activeProjectCount);
        workloadInfo.put("isOverloaded", totalWorkload.compareTo(BigDecimal.valueOf(100)) > 0);
        
        return workloadInfo;
    }
    
    public Map<String, String>[] getAvailableRoles() {
        User.Role[] roles = User.Role.values();
        Map<String, String>[] roleList = new Map[roles.length];
        
        for (int i = 0; i < roles.length; i++) {
            Map<String, String> roleMap = new HashMap<>();
            roleMap.put("id", roles[i].name());
            roleMap.put("name", roles[i].name());
            roleMap.put("description", getRoleDescription(roles[i]));
            roleList[i] = roleMap;
        }
        
        return roleList;
    }
    
    private String getRoleDescription(User.Role role) {
        switch (role) {
            case admin:
                return "Quản trị viên";
            case pm:
                return "Quản lý dự án";
            case dev:
                return "Lập trình viên";
            case ba:
                return "Phân tích nghiệp vụ";
            case test:
                return "Kiểm thử viên";
            default:
                return role.name();
        }
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
    
    /**
     * Get all active PMs with their project counts and workloads
     * @return List of PM information DTOs
     */
    public List<PmInfoResponseDto> getAllPMsInfo() {
        List<User> pmUsers = userRepository.findActiveUsersByRole(User.Role.pm);
        
        return pmUsers.stream()
                .map(user -> {
                    // Get active project count
                    Integer activeProjectCount = projectMemberRepository.countActiveProjectsByUserId(user.getId());
                    if (activeProjectCount == null) {
                        activeProjectCount = 0;
                    }
                    
                    // Get total workload
                    BigDecimal totalWorkload = projectMemberRepository.getTotalWorkloadByUserId(user.getId());
                    if (totalWorkload == null) {
                        totalWorkload = BigDecimal.ZERO;
                    }
                    
                    return PmInfoResponseDto.builder()
                            .id(user.getId())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .activeProjectCount(activeProjectCount)
                            .totalWorkload(totalWorkload)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
