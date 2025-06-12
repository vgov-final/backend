package org.viettel.vgov.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.viettel.vgov.model.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead);
    
    Page<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead, Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    long countUnreadNotificationsByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadForUser(@Param("userId") Long userId);
    
    List<Notification> findByNotificationTypeAndRelatedProjectId(String notificationType, Long relatedProjectId);
    
    List<Notification> findByRelatedProjectId(Long relatedProjectId);
    
    List<Notification> findByRelatedUserId(Long relatedUserId);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.notificationType = :type ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndNotificationType(@Param("userId") Long userId, @Param("type") String notificationType);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.notificationType = :type ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdAndNotificationType(@Param("userId") Long userId, @Param("type") String notificationType, Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND " +
           "(:isRead IS NULL OR n.isRead = :isRead) AND " +
           "(:notificationType IS NULL OR n.notificationType = :notificationType)")
    Page<Notification> findByUserIdWithFilters(@Param("userId") Long userId,
                                               @Param("isRead") Boolean isRead,
                                               @Param("notificationType") String notificationType,
                                               Pageable pageable);
    
    void deleteByUserId(Long userId);
    
    void deleteByRelatedProjectId(Long projectId);
}
