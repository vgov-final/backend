package org.viettel.vgov.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponseDto {
    
    // Project analytics
    private Long totalProjects;
    private Map<String, Long> projectsByStatus;
    private Map<String, Long> projectsByType;
    private Long activeProjects;
    private Long completedProjects;
    
    // Employee analytics
    private Long totalEmployees;
    private Map<String, Long> employeesByRole;
    private Long activeEmployees;
    private BigDecimal averageWorkload;
    
    // Workload analytics
    private Map<String, BigDecimal> workloadByRole;
    private List<UserWorkloadDto> topWorkloadUsers;
    private BigDecimal systemWorkloadUtilization;
    
    // Timeline data
    private List<ProjectTimelineDto> projectMilestones;
    private List<WorkLogSummaryDto> workLogTrends;
    private List<MonthlyProjectStatusDto> monthlyProjectStatus;
    
    // Recent activity
    private List<RecentActivityDto> recentActivities;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserWorkloadDto {
        private Long userId;
        private String userName;
        private String email;
        private BigDecimal totalWorkload;
        private Integer projectCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectTimelineDto {
        private Long projectId;
        private String projectName;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private BigDecimal completionPercentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkLogSummaryDto {
        private LocalDate date;
        private BigDecimal totalHours;
        private Integer logCount;
        private Long projectId;
        private String projectName;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDto {
        private String activityType;
        private String description;
        private LocalDate activityDate;
        private String userName;
        private String projectName;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyProjectStatusDto {
        private String month; // Format: "YYYY-MM"
        private Integer completed;
        private Integer inProgress;
        private Integer planned;
    }
}
