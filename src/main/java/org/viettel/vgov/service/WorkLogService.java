package org.viettel.vgov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viettel.vgov.dto.request.WorkLogRequestDto;
import org.viettel.vgov.dto.response.WorkLogResponseDto;
import org.viettel.vgov.exception.ResourceNotFoundException;
import org.viettel.vgov.mapper.WorkLogMapper;
import org.viettel.vgov.model.Project;
import org.viettel.vgov.model.User;
import org.viettel.vgov.model.WorkLog;
import org.viettel.vgov.repository.ProjectRepository;
import org.viettel.vgov.repository.UserRepository;
import org.viettel.vgov.repository.WorkLogRepository;
import org.viettel.vgov.security.UserPrincipal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkLogService {
    
    private final WorkLogRepository workLogRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WorkLogMapper workLogMapper;
    
    public List<WorkLogResponseDto> getAllWorkLogs() {
        return getAllWorkLogs(null, null, null, null, null, null, null, null, "workDate", "desc");
    }
    
    public List<WorkLogResponseDto> getAllWorkLogs(String search, Long projectId, Long userId,
            String workDateFrom, String workDateTo, Double minHours, Double maxHours,
            String taskFeature, String sortBy, String sortDir) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<WorkLog> workLogs;
        
        switch (currentUser.getRole()) {
            case admin:
                // Admin can see all work logs
                workLogs = workLogRepository.findAll();
                break;
            case pm:
                // PM can see work logs for projects they manage
                workLogs = workLogRepository.findWorkLogsByPmEmail(currentUser.getEmail());
                break;
            case dev:
            case ba:
            case test:
                // Employee can see only their own work logs
                workLogs = workLogRepository.findByUserId(currentUser.getId());
                break;
            default:
                throw new AccessDeniedException("Access denied");
        }
        
        // Apply filters
        workLogs = applyFilters(workLogs, search, projectId, userId, workDateFrom, workDateTo,
                               minHours, maxHours, taskFeature);
        
        // Apply sorting
        workLogs = applySorting(workLogs, sortBy, sortDir);
        
        return workLogs.stream()
                .map(workLogMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    private List<WorkLog> applyFilters(List<WorkLog> workLogs, String search, Long projectId, Long userId,
                                      String workDateFrom, String workDateTo, Double minHours, Double maxHours,
                                      String taskFeature) {
        return workLogs.stream()
                .filter(workLog -> {
                    // Search filter (search in task feature and work description)
                    if (search != null && !search.trim().isEmpty()) {
                        String searchLower = search.toLowerCase();
                        boolean matchesTask = workLog.getTaskFeature() != null &&
                                            workLog.getTaskFeature().toLowerCase().contains(searchLower);
                        boolean matchesDescription = workLog.getWorkDescription() != null &&
                                             workLog.getWorkDescription().toLowerCase().contains(searchLower);
                        if (!matchesTask && !matchesDescription) {
                            return false;
                        }
                    }
                    
                    // Project filter
                    if (projectId != null && !projectId.equals(workLog.getProject().getId())) {
                        return false;
                    }
                    
                    // User filter
                    if (userId != null && !userId.equals(workLog.getUser().getId())) {
                        return false;
                    }
                    
                    // Date range filter
                    if (workDateFrom != null) {
                        try {
                            LocalDate fromDate = LocalDate.parse(workDateFrom);
                            if (workLog.getWorkDate().isBefore(fromDate)) {
                                return false;
                            }
                        } catch (Exception e) {
                            // Invalid date format, skip filter
                        }
                    }
                    
                    if (workDateTo != null) {
                        try {
                            LocalDate toDate = LocalDate.parse(workDateTo);
                            if (workLog.getWorkDate().isAfter(toDate)) {
                                return false;
                            }
                        } catch (Exception e) {
                            // Invalid date format, skip filter
                        }
                    }
                    
                    // Hours range filter
                    if (minHours != null && workLog.getHoursWorked().doubleValue() < minHours) {
                        return false;
                    }
                    
                    if (maxHours != null && workLog.getHoursWorked().doubleValue() > maxHours) {
                        return false;
                    }
                    
                    // Task feature filter
                    if (taskFeature != null && !taskFeature.trim().isEmpty()) {
                        String featureLower = taskFeature.toLowerCase();
                        boolean matchesTask = workLog.getTaskFeature() != null &&
                                            workLog.getTaskFeature().toLowerCase().contains(featureLower);
                        if (!matchesTask) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    private List<WorkLog> applySorting(List<WorkLog> workLogs, String sortBy, String sortDir) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "workDate";
        }
        
        if (sortDir == null || sortDir.trim().isEmpty()) {
            sortDir = "desc";
        }
        
        Comparator<WorkLog> comparator;
        
        switch (sortBy.toLowerCase()) {
            case "workdate":
                comparator = Comparator.comparing(WorkLog::getWorkDate);
                break;
            case "hours":
                comparator = Comparator.comparing(WorkLog::getHoursWorked);
                break;
            case "project":
                comparator = Comparator.<WorkLog, String>comparing(wl -> wl.getProject().getProjectName());
                break;
            case "user":
                comparator = Comparator.<WorkLog, String>comparing(wl -> wl.getUser().getFullName());
                break;
            case "task":
                comparator = Comparator.comparing(WorkLog::getTaskFeature, Comparator.nullsLast(String::compareTo));
                break;
            default:
                comparator = Comparator.comparing(WorkLog::getWorkDate);
                break;
        }
        
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        
        return workLogs.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    public List<WorkLogResponseDto> getWorkLogsByUserId(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Check access permission
        boolean hasAccess = false;
        switch (currentUser.getRole()) {
            case admin:
                hasAccess = true;
                break;
            case pm:
                // PM can view work logs for users in their projects
                List<Project> pmProjects = projectRepository.findByPmEmail(currentUser.getEmail());
                hasAccess = pmProjects.stream()
                        .anyMatch(project -> projectRepository.findProjectsByUserId(userId)
                                .stream()
                                .anyMatch(userProject -> userProject.getId().equals(project.getId())));
                break;
            case dev:
            case ba:
            case test:
                // Employee can only see their own work logs
                hasAccess = currentUser.getId().equals(userId);
                break;
        }
        
        if (!hasAccess) {
            throw new AccessDeniedException("Access denied to view work logs for this user");
        }
        
        List<WorkLog> workLogs = workLogRepository.findByUserIdWithDetails(userId);
        return workLogs.stream()
                .map(workLogMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<WorkLogResponseDto> getWorkLogsByProjectId(Long projectId) {
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
            throw new AccessDeniedException("Access denied to view work logs for this project");
        }
        
        List<WorkLog> workLogs = workLogRepository.findByProjectIdWithDetails(projectId);
        return workLogs.stream()
                .map(workLogMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public WorkLogResponseDto createWorkLog(WorkLogRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Admin cannot create work logs
        if (currentUser.getRole() == User.Role.admin) {
            throw new AccessDeniedException("Admin users cannot create work logs");
        }
        
        Project project = projectRepository.findById(requestDto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + requestDto.getProjectId()));
        
        // Determine target user for the work log
        User targetUser;
        if (requestDto.getUserId() != null) {
            // PM is creating work log for another user
            if (currentUser.getRole() != User.Role.pm) {
                throw new AccessDeniedException("Only Project Managers can create work logs for other users");
            }
            
            // Check if current user is the PM of this project
            if (!project.getPmEmail().equals(currentUser.getEmail())) {
                throw new AccessDeniedException("You can only create work logs for projects you manage");
            }
            
            targetUser = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + requestDto.getUserId()));
            
            // Check if target user is assigned to this project
            boolean isTargetUserProjectMember = projectRepository.findProjectsByUserId(targetUser.getId())
                    .stream()
                    .anyMatch(p -> p.getId().equals(requestDto.getProjectId()));
            
            if (!isTargetUserProjectMember) {
                throw new AccessDeniedException("Target user is not assigned to this project");
            }
            
        } else {
            // User is creating work log for themselves
            targetUser = currentUser;
            
            // Check if user is assigned to this project
            boolean isProjectMember = projectRepository.findProjectsByUserId(currentUser.getId())
                    .stream()
                    .anyMatch(p -> p.getId().equals(requestDto.getProjectId()));
            
            if (!isProjectMember) {
                throw new AccessDeniedException("You are not assigned to this project");
            }
        }
        
        // Check if work log already exists for this user, project, and date
        if (workLogRepository.findByUserIdAndProjectIdAndWorkDate(
                targetUser.getId(), requestDto.getProjectId(), requestDto.getWorkDate()).isPresent()) {
            throw new IllegalArgumentException("Work log already exists for this date and project");
        }
        
        // Validate hours worked
        if (requestDto.getHoursWorked().compareTo(new BigDecimal("24")) > 0) {
            throw new IllegalArgumentException("Hours worked cannot exceed 24 hours per day");
        }
        
        // Validate work date is within project timeline
        if (requestDto.getWorkDate().isBefore(project.getStartDate()) ||
            (project.getEndDate() != null && requestDto.getWorkDate().isAfter(project.getEndDate()))) {
            throw new IllegalArgumentException("Work date must be within project timeline");
        }
        
        WorkLog workLog = workLogMapper.toEntity(requestDto);
        workLog.setUser(targetUser);
        workLog.setProject(project);
        
        WorkLog savedWorkLog = workLogRepository.save(workLog);
        return workLogMapper.toResponseDto(savedWorkLog);
    }
    
    public WorkLogResponseDto updateWorkLog(Long id, WorkLogRequestDto requestDto) {
        WorkLog workLog = workLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work log not found with id: " + id));
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Admin cannot update work logs
        if (currentUser.getRole() == User.Role.admin) {
            throw new AccessDeniedException("Admin users cannot update work logs");
        }
        
        // Check if user can update this work log
        boolean canUpdate = false;
        if (workLog.getUser().getId().equals(currentUser.getId())) {
            // User can update their own work log
            canUpdate = true;
        } else if (currentUser.getRole() == User.Role.pm) {
            // PM can update work logs for users in their projects
            Project project = workLog.getProject();
            if (project.getPmEmail().equals(currentUser.getEmail())) {
                canUpdate = true;
            }
        }
        
        if (!canUpdate) {
            throw new AccessDeniedException("You can only update your own work logs or work logs of employees in projects you manage");
        }
        
        // Validate hours worked
        if (requestDto.getHoursWorked().compareTo(new BigDecimal("24")) > 0) {
            throw new IllegalArgumentException("Hours worked cannot exceed 24 hours per day");
        }
        
        // Validate work date is within project timeline
        Project project = workLog.getProject();
        if (requestDto.getWorkDate().isBefore(project.getStartDate()) ||
            (project.getEndDate() != null && requestDto.getWorkDate().isAfter(project.getEndDate()))) {
            throw new IllegalArgumentException("Work date must be within project timeline");
        }
        
        // Check if changing date conflicts with existing work log
        if (!workLog.getWorkDate().equals(requestDto.getWorkDate())) {
            if (workLogRepository.findByUserIdAndProjectIdAndWorkDate(
                    workLog.getUser().getId(), workLog.getProject().getId(), requestDto.getWorkDate()).isPresent()) {
                throw new IllegalArgumentException("Work log already exists for this date and project");
            }
        }
        
        // Update work log fields
        workLog.setWorkDate(requestDto.getWorkDate());
        workLog.setHoursWorked(requestDto.getHoursWorked());
        workLog.setTaskFeature(requestDto.getTaskFeature());
        workLog.setWorkDescription(requestDto.getWorkDescription());
        
        WorkLog savedWorkLog = workLogRepository.save(workLog);
        return workLogMapper.toResponseDto(savedWorkLog);
    }
    
    public void deleteWorkLog(Long id) {
        WorkLog workLog = workLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work log not found with id: " + id));
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Admin cannot delete work logs
        if (currentUser.getRole() == User.Role.admin) {
            throw new AccessDeniedException("Admin users cannot delete work logs");
        }
        
        // Check if user can delete this work log
        boolean canDelete = false;
        if (workLog.getUser().getId().equals(currentUser.getId())) {
            // User can delete their own work log
            canDelete = true;
        } else if (currentUser.getRole() == User.Role.pm) {
            // PM can delete work logs for users in their projects
            Project project = workLog.getProject();
            if (project.getPmEmail().equals(currentUser.getEmail())) {
                canDelete = true;
            }
        }
        
        if (!canDelete) {
            throw new AccessDeniedException("You can only delete your own work logs or work logs of employees in projects you manage");
        }
        
        workLogRepository.delete(workLog);
    }
    
    public boolean canAccessUserWorkLogs(Long userId, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        switch (currentUser.getRole()) {
            case admin:
                return true;
            case pm:
                // PM can view work logs for users in their projects
                List<Project> pmProjects = projectRepository.findByPmEmail(currentUser.getEmail());
                return pmProjects.stream()
                        .anyMatch(project -> projectRepository.findProjectsByUserId(userId)
                                .stream()
                                .anyMatch(userProject -> userProject.getId().equals(project.getId())));
            case dev:
            case ba:
            case test:
                // Employee can only see their own work logs
                return currentUser.getId().equals(userId);
            default:
                return false;
        }
    }
}
