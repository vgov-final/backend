package org.viettel.vgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.viettel.vgov.model.ProjectMember;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    
    List<ProjectMember> findByProjectId(Long projectId);
    
    List<ProjectMember> findByUserId(Long userId);
    
    List<ProjectMember> findByProjectIdAndIsActiveTrue(Long projectId);
    
    List<ProjectMember> findByUserIdAndIsActiveTrue(Long userId);
    
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);
    
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.user.id = :userId AND pm.isActive = true AND pm.project.status != 'Closed'")
    List<ProjectMember> findActiveProjectMembersByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(pm.workloadPercentage) FROM ProjectMember pm WHERE pm.user.id = :userId AND pm.isActive = true AND pm.project.status != 'Closed'")
    BigDecimal getTotalWorkloadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.user JOIN FETCH pm.project WHERE pm.project.id = :projectId AND pm.isActive = true")
    List<ProjectMember> findActiveProjectMembersWithDetailsByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.user JOIN FETCH pm.project WHERE pm.user.id = :userId AND pm.isActive = true AND pm.project.status != 'Closed'")
    List<ProjectMember> findActiveProjectMembersWithDetailsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(pm) FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.isActive = true")
    long countActiveProjectMembersByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.pmEmail = :pmEmail AND pm.isActive = true AND pm.project.status != 'Closed'")
    List<ProjectMember> findActiveProjectMembersByPmEmail(@Param("pmEmail") String pmEmail);
    
    boolean existsByProjectIdAndUserIdAndIsActiveTrue(Long projectId, Long userId);
    
    // Additional methods for ProjectMemberService
    List<ProjectMember> findByProjectIdAndIsActive(Long projectId, Boolean isActive);
    
    boolean existsByProjectIdAndUserIdAndIsActive(Long projectId, Long userId, Boolean isActive);
    
    Optional<ProjectMember> findByProjectIdAndUserIdAndIsActive(Long projectId, Long userId, Boolean isActive);
    
    @Query("SELECT SUM(pm.workloadPercentage) FROM ProjectMember pm WHERE pm.user.id = :userId AND pm.isActive = true AND pm.project.status != 'Closed' AND pm.id != :excludeId")
    BigDecimal getTotalWorkloadByUserIdExcluding(@Param("userId") Long userId, @Param("excludeId") Long excludeId);
    
    @Query("SELECT COUNT(pm.project) FROM ProjectMember pm WHERE pm.user.id = :userId AND pm.isActive = true AND pm.project.status != 'Closed'")
    Integer countActiveProjectsByUserId(@Param("userId") Long userId);
}
