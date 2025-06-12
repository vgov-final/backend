package org.viettel.vgov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viettel.vgov.dto.request.ProjectMemberRequestDto;
import org.viettel.vgov.dto.response.ProjectMemberResponseDto;
import org.viettel.vgov.dto.response.UserResponseDto;
import org.viettel.vgov.dto.response.WorkloadHistoryResponseDto;
import org.viettel.vgov.exception.ResourceNotFoundException;
import org.viettel.vgov.mapper.ProjectMemberMapper;
import org.viettel.vgov.mapper.UserMapper;
import org.viettel.vgov.mapper.WorkloadHistoryMapper;
import org.viettel.vgov.model.Project;
import org.viettel.vgov.model.ProjectMember;
import org.viettel.vgov.model.User;
import org.viettel.vgov.model.WorkloadHistory;
import org.viettel.vgov.repository.ProjectMemberRepository;
import org.viettel.vgov.repository.ProjectRepository;
import org.viettel.vgov.repository.UserRepository;
import org.viettel.vgov.repository.WorkloadHistoryRepository;
import org.viettel.vgov.security.UserPrincipal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserMapper userMapper;
    private final WorkloadHistoryRepository workloadHistoryRepository;
    private final WorkloadHistoryMapper workloadHistoryMapper;
    private final NotificationService notificationService;

    public List<ProjectMemberResponseDto> getProjectMembers(Long projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // Check access permission
        boolean hasAccess = false;
        switch (currentUser.getRole()) {
            case admin:
                hasAccess = true;
                break;
            case pm:
                hasAccess = project.getPmEmail().equals(currentUser.getEmail());
                break;
            case dev:
            case ba:
            case test:
                hasAccess = projectRepository.findProjectsByUserId(currentUser.getId())
                        .stream()
                        .anyMatch(p -> p.getId().equals(projectId));
                break;
        }

        if (!hasAccess) {
            throw new AccessDeniedException("Access denied to this project");
        }

        // For closed projects, show all members (including inactive ones who completed the project)
        // For active projects, show only active members
        List<ProjectMember> members;
        if (project.getStatus() == Project.Status.Closed) {
            members = projectMemberRepository.findByProjectId(projectId);
        } else {
            members = projectMemberRepository.findByProjectIdAndIsActive(projectId, true);
        }

        return members.stream()
                .map(projectMemberMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public ProjectMemberResponseDto addMemberToProject(Long projectId, ProjectMemberRequestDto requestDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDto.getUserId()));

        // Admin users cannot be added to projects
        if (user.getRole() == User.Role.admin) {
            throw new IllegalArgumentException("Admin users cannot be added to projects");
        }

        // Check if user is already a member of this project
        if (projectMemberRepository.existsByProjectIdAndUserIdAndIsActive(projectId, requestDto.getUserId(), true)) {
            throw new IllegalArgumentException("User is already a member of this project");
        }

        // Check workload limit (max 100%)
        BigDecimal totalWorkload = projectMemberRepository.getTotalWorkloadByUserId(requestDto.getUserId());
        if (totalWorkload == null) {
            totalWorkload = BigDecimal.ZERO;
        }

        if (totalWorkload.add(requestDto.getWorkloadPercentage()).compareTo(new BigDecimal("100")) > 0) {
            throw new org.viettel.vgov.exception.WorkloadExceededException("Total workload cannot exceed 100% for user: " + user.getFullName());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProjectMember projectMember = projectMemberMapper.toEntity(requestDto);
        projectMember.setProject(project);
        projectMember.setUser(user);
        projectMember.setCreatedBy(currentUser);

        if (projectMember.getJoinedDate() == null) {
            projectMember.setJoinedDate(LocalDate.now());
        }

        ProjectMember savedMember = projectMemberRepository.save(projectMember);

        notificationService.notifyUserAddedToProject(project, user, currentUser);

        return projectMemberMapper.toResponseDto(savedMember);
    }

    public ProjectMemberResponseDto updateMemberWorkload(Long projectId, Long userId, ProjectMemberRequestDto requestDto) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserIdAndIsActive(projectId, userId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Project member not found"));

        // Check workload limit excluding current assignment
        BigDecimal currentWorkload = projectMemberRepository.getTotalWorkloadByUserIdExcluding(userId, projectMember.getId());
        if (currentWorkload == null) {
            currentWorkload = BigDecimal.ZERO;
        }

        if (currentWorkload.add(requestDto.getWorkloadPercentage()).compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Total workload cannot exceed 100% for this user");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Save history before updating
        WorkloadHistory history = WorkloadHistory.builder()
                .projectMember(projectMember)
                .oldWorkloadPercentage(projectMember.getWorkloadPercentage())
                .newWorkloadPercentage(requestDto.getWorkloadPercentage())
                .reason(requestDto.getReason())
                .changedBy(currentUser)
                .changeTimestamp(LocalDateTime.now())
                .build();
        workloadHistoryRepository.save(history);

        projectMember.setWorkloadPercentage(requestDto.getWorkloadPercentage());
        projectMember.setUpdatedBy(currentUser);

        ProjectMember savedMember = projectMemberRepository.save(projectMember);
        return projectMemberMapper.toResponseDto(savedMember);
    }

    public void removeMemberFromProject(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserIdAndIsActive(projectId, userId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Project member not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        projectMember.setIsActive(false);
        projectMember.setLeftDate(LocalDate.now());
        projectMember.setUpdatedBy(currentUser);

        projectMemberRepository.save(projectMember);

        // TODO: Send notification to all project members about member removal
    }

    public UserResponseDto getUserWorkload(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserResponseDto userDto = userMapper.toResponseDto(user);

        // Get current workload
        BigDecimal totalWorkload = projectMemberRepository.getTotalWorkloadByUserId(userId);
        if (totalWorkload == null) {
            totalWorkload = BigDecimal.ZERO;
        }

        // TODO: Add workload field to UserResponseDto or create separate WorkloadResponseDto

        return userDto;
    }

    public List<WorkloadHistoryResponseDto> getWorkloadHistory(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserIdAndIsActive(projectId, userId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Project member not found"));

        List<WorkloadHistory> historyList = workloadHistoryRepository
                .findByProjectMember_IdOrderByChangeTimestampDesc(projectMember.getId());

        return historyList.stream()
                .map(workloadHistoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
