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
import org.viettel.vgov.model.ProjectMember;
import org.viettel.vgov.model.User;
import org.viettel.vgov.repository.NotificationRepository;
import org.viettel.vgov.repository.ProjectMemberRepository;
import org.viettel.vgov.security.UserPrincipal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final NotificationMapper notificationMapper;

    public Page<NotificationResponseDto> getCurrentUserNotifications(Pageable pageable, Boolean isRead, String notificationType) {
        UserPrincipal userPrincipal = getCurrentUserPrincipal();
        Page<Notification> notifications = notificationRepository.findByUserIdWithFilters(
                userPrincipal.getId(), isRead, notificationType, pageable);
        return notifications.map(notificationMapper::toResponseDto);
    }

    public List<NotificationResponseDto> getAllCurrentUserNotifications() {
        UserPrincipal userPrincipal = getCurrentUserPrincipal();
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userPrincipal.getId());
        return notifications.stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDto> getUnreadNotifications() {
        UserPrincipal userPrincipal = getCurrentUserPrincipal();
        List<Notification> notifications = notificationRepository.findUnreadNotificationsByUserId(userPrincipal.getId());
        return notifications.stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public long getUnreadCount() {
        UserPrincipal userPrincipal = getCurrentUserPrincipal();
        return notificationRepository.countUnreadNotificationsByUserId(userPrincipal.getId());
    }

    public void markAsRead(Long notificationId) {
        UserPrincipal userPrincipal = getCurrentUserPrincipal();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(userPrincipal.getId())) {
            throw new ResourceNotFoundException("Notification not found for current user with id: " + notificationId);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead() {
        UserPrincipal userPrincipal = getCurrentUserPrincipal();
        notificationRepository.markAllAsReadForUser(userPrincipal.getId());
    }

    public void deleteNotification(Long notificationId) {
        UserPrincipal userPrincipal = getCurrentUserPrincipal();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(userPrincipal.getId())) {
            throw new ResourceNotFoundException("Notification not found for current user with id: " + notificationId);
        }

        notificationRepository.delete(notification);
    }

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

    public void createProjectNotification(Project project, String title, String message, String notificationType, User originatorUser) {
        List<ProjectMember> activeMembers = projectMemberRepository.findByProjectIdAndIsActiveTrue(project.getId());
        List<Notification> notificationsToSave = new ArrayList<>();

        for (ProjectMember member : activeMembers) {
            if (originatorUser != null && member.getUser().getId().equals(originatorUser.getId())) {
                continue;
            }

            Notification notification = new Notification();
            notification.setUser(member.getUser());
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setNotificationType(notificationType);
            notification.setRelatedProject(project);
            notification.setRelatedUser(originatorUser);
            notification.setIsRead(false);
            notificationsToSave.add(notification);
        }

        if (!notificationsToSave.isEmpty()) {
            notificationRepository.saveAll(notificationsToSave);
        }
    }

    public void notifyUserAddedToProject(Project project, User newUser, User addedBy) {
        String title = "Bạn đã được thêm vào dự án mới";
        String message = String.format("Bạn đã được %s thêm vào dự án %s.", addedBy.getFullName(), project.getProjectName());
        createNotification(newUser, title, message, "project", project, addedBy);
    }

    private UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("No authenticated user found");
        }
        return (UserPrincipal) authentication.getPrincipal();
    }
}
