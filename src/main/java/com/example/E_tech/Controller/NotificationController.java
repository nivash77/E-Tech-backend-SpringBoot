package com.example.E_tech.Controller;

import com.example.E_tech.Entity.Notification;
import com.example.E_tech.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    //Get all notifications for a user
    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestHeader("email") String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            List<Notification> notifications = notificationService.getNotifications(email, page, size);
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Mark a single notification as read
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id, @RequestHeader("email") String email) {
        try {
            Notification updatedNotification = notificationService.markAsRead(id, email);
            return ResponseEntity.ok(updatedNotification);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Mark all notifications as read for a user
    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestHeader("email") String email) {
        try {
            List<Notification> updatedNotifications = notificationService.markAllAsRead(email);
            return ResponseEntity.ok(updatedNotifications);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Delete a single notification
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable String id, @RequestHeader("email") String email) {
        try {
            notificationService.deleteNotification(id, email);
            return ResponseEntity.ok("Notification deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Delete all notifications for a user
    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAllNotifications(@RequestHeader("email") String email) {
        try {
            notificationService.deleteAllNotifications(email);
            return ResponseEntity.ok("All notifications deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Create a new notification (for testing)
    @PostMapping("/create")
    public ResponseEntity<?> createNotification(@RequestHeader("email") String email,
                                                @RequestParam String type,
                                                @RequestParam String message) {
        try {
            Notification notification = notificationService.createNotificationForEmail(email, type, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/createForGarde")
    public ResponseEntity<?> createNotifications( @RequestParam String type,
                                                 @RequestParam String message,
                                                 @RequestParam String submissionId){
        try {
            Notification notification = notificationService.createNotificationForGradeEmail(submissionId, type, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
