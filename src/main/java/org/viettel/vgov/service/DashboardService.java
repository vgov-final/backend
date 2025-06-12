package org.viettel.vgov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viettel.vgov.dto.response.DashboardResponseDto;
import org.viettel.vgov.dto.response.ProjectResponseDto;
import org.viettel.vgov.dto.response.WorkLogResponseDto;
import org.viettel.vgov.exception.ResourceNotFoundException;
import org.viettel.vgov.mapper.ProjectMapper;
import org.viettel.vgov.mapper.WorkLogMapper;
import org.viettel.vgov.model.Project;
import org.viettel.vgov.model.User;
import org.viettel.vgov.model.WorkLog;
import org.viettel.vgov.repository.*;
import org.viettel.vgov.security.UserPrincipal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {
    
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final WorkLogRepository workLogRepository;
    private final NotificationRepository notificationRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMapper projectMapper;
    private final WorkLogMapper workLogMapper;
    
    public DashboardResponseDto getDashboardData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        DashboardResponseDto dashboard = new DashboardResponseDto();
        
        // Get user stats (only for admin)
        if (currentUser.getRole() == User.Role.admin) {
            dashboard.setUserStats(getUserStats());
        }
        
        // Get project stats
        dashboard.setProjectStats(getProjectStats(currentUser));
        
        // Get work log stats
        dashboard.setWorkLogStats(getWorkLogStats(currentUser));
        
        // Get recent projects
        dashboard.setRecentProjects(getRecentProjects(currentUser));
        
        // Get recent work logs
        dashboard.setRecentWorkLogs(getRecentWorkLogs(currentUser));
        
        // Get unread notification count
        dashboard.setUnreadNotificationCount(notificationRepository.countUnreadNotificationsByUserId(currentUser.getId()));
        
        return dashboard;
    }
    
    private DashboardResponseDto.UserStats getUserStats() {
        DashboardResponseDto.UserStats userStats = new DashboardResponseDto.UserStats();
        
        userStats.setTotalUsers(userRepository.count());
        userStats.setActiveUsers(userRepository.countByIsActive(true));
        userStats.setAdminUsers(userRepository.countByRole(User.Role.admin));
        userStats.setPmUsers(userRepository.countByRole(User.Role.pm));
        userStats.setDevUsers(userRepository.countByRole(User.Role.dev));
        userStats.setBaUsers(userRepository.countByRole(User.Role.ba));
        userStats.setTestUsers(userRepository.countByRole(User.Role.test));
        
        return userStats;
    }
    
    private DashboardResponseDto.ProjectStats getProjectStats(User currentUser) {
        DashboardResponseDto.ProjectStats projectStats = new DashboardResponseDto.ProjectStats();
        
        List<Project> userProjects = getUserAccessibleProjects(currentUser);
        
        projectStats.setTotalProjects(userProjects.size());
        projectStats.setActiveProjects(userProjects.stream()
                .filter(p -> p.getStatus() == Project.Status.InProgress)
                .count());
        projectStats.setClosedProjects(userProjects.stream()
                .filter(p -> p.getStatus() == Project.Status.Closed)
                .count());
        projectStats.setOnHoldProjects(userProjects.stream()
                .filter(p -> p.getStatus() == Project.Status.Hold)
                .count());
        
        // Projects by type
        Map<String, Long> projectsByType = new HashMap<>();
        for (Project.ProjectType type : Project.ProjectType.values()) {
            long count = userProjects.stream()
                    .filter(p -> p.getProjectType() == type)
                    .count();
            projectsByType.put(type.name(), count);
        }
        projectStats.setProjectsByType(projectsByType);
        
        // Projects by status
        Map<String, Long> projectsByStatus = new HashMap<>();
        for (Project.Status status : Project.Status.values()) {
            long count = userProjects.stream()
                    .filter(p -> p.getStatus() == status)
                    .count();
            projectsByStatus.put(status.name(), count);
        }
        projectStats.setProjectsByStatus(projectsByStatus);
        
        return projectStats;
    }
    
    private DashboardResponseDto.WorkLogStats getWorkLogStats(User currentUser) {
        DashboardResponseDto.WorkLogStats workLogStats = new DashboardResponseDto.WorkLogStats();
        
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDate endOfLastMonth = startOfMonth.minusDays(1);
        
        List<WorkLog> userWorkLogs = getUserAccessibleWorkLogs(currentUser);
        
        // Total hours this month
        BigDecimal thisMonthHours = userWorkLogs.stream()
                .filter(wl -> !wl.getWorkDate().isBefore(startOfMonth))
                .map(WorkLog::getHoursWorked)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        workLogStats.setTotalHoursThisMonth(thisMonthHours);
        
        // Total hours last month
        BigDecimal lastMonthHours = userWorkLogs.stream()
                .filter(wl -> !wl.getWorkDate().isBefore(startOfLastMonth) && !wl.getWorkDate().isAfter(endOfLastMonth))
                .map(WorkLog::getHoursWorked)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        workLogStats.setTotalHoursLastMonth(lastMonthHours);
        
        // Average hours per day (this month)
        long daysInMonth = now.getDayOfMonth();
        BigDecimal averageHours = daysInMonth > 0 ? 
                thisMonthHours.divide(BigDecimal.valueOf(daysInMonth), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        workLogStats.setAverageHoursPerDay(averageHours);
        
        workLogStats.setTotalWorkLogs(userWorkLogs.size());
        
        // Hours by project
        Map<String, BigDecimal> hoursByProject = userWorkLogs.stream()
                .collect(Collectors.groupingBy(
                        wl -> wl.getProject().getProjectName(),
                        Collectors.reducing(BigDecimal.ZERO, WorkLog::getHoursWorked, BigDecimal::add)
                ));
        workLogStats.setHoursByProject(hoursByProject);
        
        return workLogStats;
    }
    
    private List<ProjectResponseDto> getRecentProjects(User currentUser) {
        List<Project> recentProjects = getUserAccessibleProjects(currentUser).stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
        
        return recentProjects.stream()
                .map(projectMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    private List<WorkLogResponseDto> getRecentWorkLogs(User currentUser) {
        List<WorkLog> recentWorkLogs = getUserAccessibleWorkLogs(currentUser).stream()
                .sorted((wl1, wl2) -> wl2.getCreatedAt().compareTo(wl1.getCreatedAt()))
                .limit(10)
                .collect(Collectors.toList());
        
        return recentWorkLogs.stream()
                .map(workLogMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    private List<Project> getUserAccessibleProjects(User currentUser) {
        switch (currentUser.getRole()) {
            case admin:
                return projectRepository.findAll();
            case pm:
                return projectRepository.findByPmEmail(currentUser.getEmail());
            case dev:
            case ba:
            case test:
                return projectRepository.findProjectsByUserId(currentUser.getId());
            default:
                return List.of();
        }
    }
    
    private List<WorkLog> getUserAccessibleWorkLogs(User currentUser) {
        switch (currentUser.getRole()) {
            case admin:
                return workLogRepository.findAll();
            case pm:
                return workLogRepository.findWorkLogsByPmEmail(currentUser.getEmail());
            case dev:
            case ba:
            case test:
                return workLogRepository.findByUserId(currentUser.getId());
            default:
                return List.of();
        }
    }
}
