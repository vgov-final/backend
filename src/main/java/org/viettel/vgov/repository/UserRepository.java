package org.viettel.vgov.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.viettel.vgov.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmployeeCode(String employeeCode);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmployeeCode(String employeeCode);
    
    List<User> findByIsActiveTrue();
    
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    List<User> findByRole(User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role = :role")
    List<User> findActiveUsersByRole(@Param("role") User.Role role);
    
    @Query("SELECT u FROM User u JOIN FETCH u.createdBy WHERE u.id = :id")
    Optional<User> findByIdWithCreatedBy(@Param("id") Long id);
    
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:name% AND u.isActive = true")
    List<User> findByFullNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    // Count methods for dashboard
    long countByIsActive(Boolean isActive);
    
    long countByRole(User.Role role);
    
    // Filter method for user search with pagination
    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " u.fullName LIKE CONCAT('%', :search, '%') OR " +
           " u.email LIKE CONCAT('%', :search, '%') OR " +
           " u.employeeCode LIKE CONCAT('%', :search, '%')) AND " +
           "(:roleEnum IS NULL OR u.role = :roleEnum) AND " +
           "(:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> findUsersWithFilters(@Param("search") String search, 
                                   @Param("roleEnum") User.Role roleEnum, 
                                   @Param("isActive") Boolean isActive, 
                                   Pageable pageable);
}
