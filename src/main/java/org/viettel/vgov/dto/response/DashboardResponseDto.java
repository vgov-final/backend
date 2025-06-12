package org.viettel.vgov.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardResponseDto {
    
    private UserStats userStats;
    private ProjectStats projectStats;
    private WorkLogStats workLogStats;
    private List<ProjectResponseDto> recentProjects;
    private List<WorkLogResponseDto> recentWorkLogs;
    private long unreadNotificationCount;
    
    @Data
    public static class UserStats {
        private long totalUsers;
        private long activeUsers;
        private long adminUsers;
        private long pmUsers;
        private long devUsers;
        private long baUsers;
        private long testUsers;
    }
    
    @Data
    public static class ProjectStats {
        private long totalProjects;
        private long activeProjects;
        private long closedProjects;
        private long onHoldProjects;
        private Map<String, Long> projectsByType;
        private Map<String, Long> projectsByStatus;
    }
    
    @Data
    public static class WorkLogStats {
        private BigDecimal totalHoursThisMonth;
        private BigDecimal totalHoursLastMonth;
        private BigDecimal averageHoursPerDay;
        private long totalWorkLogs;
        private Map<String, BigDecimal> hoursByProject;
    }
}
