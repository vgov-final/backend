package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.viettel.vgov.dto.response.NotificationResponseDto;
import org.viettel.vgov.dto.response.PagedResponse;
import org.viettel.vgov.service.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notification management")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @Operation(summary = "Get user notifications with pagination", description = "Get paginated notifications for current user")
    @GetMapping
    public ResponseEntity<PagedResponse<NotificationResponseDto>> getCurrentUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String notificationType) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<NotificationResponseDto> notifications = notificationService.getCurrentUserNotifications(
                pageable, isRead, notificationType);
        
        PagedResponse<NotificationResponseDto> response = PagedResponse.of(notifications);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get all notifications (simple)", description = "Get all notifications for current user without pagination")
    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponseDto>> getAllCurrentUserNotifications() {
        List<NotificationResponseDto> notifications = notificationService.getAllCurrentUserNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    @Operation(summary = "Get unread notifications", description = "Get only unread notifications for current user")
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications() {
        List<NotificationResponseDto> notifications = notificationService.getUnreadNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    @Operation(summary = "Get unread count", description = "Get count of unread notifications")
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @Operation(summary = "Mark notification as read", description = "Mark specific notification as read")
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }
    
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for current user")
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }
    
    @Operation(summary = "Delete notification", description = "Delete specific notification")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
    }
}
