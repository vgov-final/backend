package org.viettel.vgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.viettel.vgov.model.WorkLog;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    
    List<WorkLog> findByUserId(Long userId);
    
    List<WorkLog> findByProjectId(Long projectId);
    
    List<WorkLog> findByWorkDate(LocalDate workDate);
    
    Optional<WorkLog> findByUserIdAndProjectIdAndWorkDate(Long userId, Long projectId, LocalDate workDate);
    
    @Query("SELECT wl FROM WorkLog wl WHERE wl.user.id = :userId AND wl.workDate BETWEEN :startDate AND :endDate ORDER BY wl.workDate DESC")
    List<WorkLog> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT wl FROM WorkLog wl WHERE wl.project.id = :projectId AND wl.workDate BETWEEN :startDate AND :endDate ORDER BY wl.workDate DESC")
    List<WorkLog> findByProjectIdAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(wl.hoursWorked) FROM WorkLog wl WHERE wl.user.id = :userId AND wl.workDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalHoursByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(wl.hoursWorked) FROM WorkLog wl WHERE wl.project.id = :projectId AND wl.workDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalHoursByProjectAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT wl FROM WorkLog wl WHERE wl.project.pmEmail = :pmEmail ORDER BY wl.workDate DESC")
    List<WorkLog> findWorkLogsByPmEmail(@Param("pmEmail") String pmEmail);
    
    @Query("SELECT wl FROM WorkLog wl JOIN FETCH wl.user JOIN FETCH wl.project WHERE wl.user.id = :userId ORDER BY wl.workDate DESC")
    List<WorkLog> findByUserIdWithDetails(@Param("userId") Long userId);
    
    @Query("SELECT wl FROM WorkLog wl JOIN FETCH wl.user JOIN FETCH wl.project WHERE wl.project.id = :projectId ORDER BY wl.workDate DESC")
    List<WorkLog> findByProjectIdWithDetails(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(wl) FROM WorkLog wl WHERE wl.project.id = :projectId")
    Long countByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT new org.viettel.vgov.dto.response.AnalyticsResponseDto$WorkLogSummaryDto(wl.workDate, SUM(wl.hoursWorked), CAST(COUNT(wl) AS int), wl.project.id, wl.project.projectName) " +
           "FROM WorkLog wl WHERE wl.project.id = :projectId " +
           "GROUP BY wl.workDate, wl.project.id, wl.project.projectName " +
           "ORDER BY wl.workDate DESC")
    List<org.viettel.vgov.dto.response.AnalyticsResponseDto.WorkLogSummaryDto> findWorkLogSummaryByProject(@Param("projectId") Long projectId);
}
