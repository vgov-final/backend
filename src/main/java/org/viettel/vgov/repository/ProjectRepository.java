package org.viettel.vgov.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.viettel.vgov.model.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    Optional<Project> findByProjectCode(String projectCode);
    
    boolean existsByProjectCode(String projectCode);
    
    List<Project> findByPmEmail(String pmEmail);
    
    Page<Project> findByPmEmail(String pmEmail, Pageable pageable);
    
    List<Project> findByStatus(Project.Status status);
    
    List<Project> findByProjectType(Project.ProjectType projectType);
    
    @Query("SELECT p FROM Project p WHERE p.pmEmail = :pmEmail AND p.status = :status")
    List<Project> findByPmEmailAndStatus(@Param("pmEmail") String pmEmail, @Param("status") Project.Status status);
    
    @Query("SELECT DISTINCT p FROM Project p JOIN p.projectMembers pm WHERE pm.user.id = :userId AND pm.isActive = true AND p.status != 'Closed'")
    List<Project> findProjectsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT p FROM Project p JOIN p.projectMembers pm WHERE pm.user.id = :userId AND pm.isActive = true AND p.status != 'Closed'")
    Page<Project> findProjectsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.projectName LIKE %:name%")
    List<Project> findByProjectNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT p FROM Project p JOIN FETCH p.createdBy WHERE p.id = :id")
    Optional<Project> findByIdWithCreatedBy(@Param("id") Long id);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status")
    long countByStatus(@Param("status") Project.Status status);
    
    @Query("SELECT p FROM Project p WHERE p.pmEmail = :pmEmail ORDER BY p.createdAt DESC")
    List<Project> findProjectsManagedByPm(@Param("pmEmail") String pmEmail);
    
    // Filter methods for admin (all projects)
    @Query("SELECT p FROM Project p WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " p.projectName LIKE CONCAT('%', :search, '%') OR " +
           " p.projectCode LIKE CONCAT('%', :search, '%') OR " +
           " p.description LIKE CONCAT('%', :search, '%')) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:projectType IS NULL OR p.projectType = :projectType)")
    Page<Project> findProjectsWithFilters(@Param("search") String search,
                                         @Param("status") Project.Status status,
                                         @Param("projectType") Project.ProjectType projectType,
                                         Pageable pageable);
    
    // Filter methods for PM (only their managed projects)
    @Query("SELECT p FROM Project p WHERE " +
           "p.pmEmail = :pmEmail AND " +
           "(:search IS NULL OR :search = '' OR " +
           " p.projectName LIKE CONCAT('%', :search, '%') OR " +
           " p.projectCode LIKE CONCAT('%', :search, '%') OR " +
           " p.description LIKE CONCAT('%', :search, '%')) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:projectType IS NULL OR p.projectType = :projectType)")
    Page<Project> findProjectsWithFiltersForPM(@Param("pmEmail") String pmEmail,
                                              @Param("search") String search,
                                              @Param("status") Project.Status status,
                                              @Param("projectType") Project.ProjectType projectType,
                                              Pageable pageable);
    
    // Filter methods for users (only assigned projects)
    @Query("SELECT DISTINCT p FROM Project p JOIN p.projectMembers pm WHERE " +
           "pm.user.id = :userId AND pm.isActive = true AND p.status != 'Closed' AND " +
           "(:search IS NULL OR :search = '' OR " +
           " p.projectName LIKE CONCAT('%', :search, '%') OR " +
           " p.projectCode LIKE CONCAT('%', :search, '%') OR " +
           " p.description LIKE CONCAT('%', :search, '%')) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:projectType IS NULL OR p.projectType = :projectType)")
    Page<Project> findProjectsWithFiltersForUser(@Param("userId") Long userId,
                                                 @Param("search") String search,
                                                 @Param("status") Project.Status status,
                                                 @Param("projectType") Project.ProjectType projectType,
                                                 Pageable pageable);
}
