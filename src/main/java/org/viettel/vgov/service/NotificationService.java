package org.viettel.vgov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viettel.vgov.dto.response.NotificationResponseDto;
import org.viettel.vgov.exception.ResourceNotFoundException;
import org.viettel.vgov.mapper.NotificationMapper;
import org.viettel.vgov.model.Notification;
import org.viettel.vgov.model.Project;
import org.viettel.vgov.model.User;
import org.viettel.vgov.repository.NotificationRepository;
import org.viettel.vgov.repository.UserRepository;
import org.viettel.vgov.security.UserPrincipal;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    
    public Page<NotificationResponseDto> getCurrentUserNotifications(Pageable pageable, Boolean isRead, String notificationType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Page<Notification> notifications = notificationRepository.findByUserIdWithFilters(
                userPrincipal.getId(), isRead, notificationType, pageable);
        
        return notifications.map(notificationMapper::toResponseDto);
    }
    
    public List<NotificationResponseDto> getAllCurrentUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userPrincipal.getId());
        return notifications.stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<NotificationResponseDto> getUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        List<Notification> notifications = notificationRepository.findUnreadNotificationsByUserId(userPrincipal.getId());
        return notifications.stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public long getUnreadCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        return notificationRepository.countUnreadNotificationsByUserId(userPrincipal.getId());
    }
    
    public void markAsRead(Long notificationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        
        // Check if the notification belongs to the current user
        if (!notification.getUser().getId().equals(userPrincipal.getId())) {
            throw new ResourceNotFoundException("Notification not found");
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    public void markAllAsRead() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        List<Notification> unreadNotifications = notificationRepository.findUnreadNotificationsByUserId(userPrincipal.getId());
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    public void deleteNotification(Long notificationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        
        // Check if the notification belongs to the current user
        if (!notification.getUser().getId().equals(userPrincipal.getId())) {
            throw new ResourceNotFoundException("Notification not found");
        }
        
        notificationRepository.delete(notification);
    }
    
    // Helper method to create notifications (used by other services)
    public void createNotification(User user, String title, String message, String notificationType, Project relatedProject, User relatedUser) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setRelatedProject(relatedProject);
        notification.setRelatedUser(relatedUser);
        notification.setIsRead(false);
        
        notificationRepository.save(notification);
    }
    
    // Helper method to create project-related notifications for all project members
    public void createProjectNotification(Project project, String title, String message, String notificationType, User relatedUser) {
        // This would require getting all project members and creating notifications for them
        // Implementation depends on ProjectMemberRepository access
    }
}
