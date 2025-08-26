package com.example.E_tech.Service;

import com.example.E_tech.Entity.Notification;
import com.example.E_tech.Entity.Submission;
import com.example.E_tech.Entity.User;
import com.example.E_tech.Repository.NotificationRepository;
import com.example.E_tech.Repository.SubmissionRepository;
import com.example.E_tech.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SubmissionRepository submissionRepository;

    //Get notifications by user email
    public List<Notification> getNotifications(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> pageResult = notificationRepository.findByUserId(user.getId(), pageable);
        return pageResult.getContent(); // convert to List
    }




    //Mark single notification as read
    public Notification markAsRead(String id, String userEmail) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!notification.getUserId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to mark this notification as read");
        }
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    //Mark all notifications as read
    public List<Notification> markAllAsRead(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Notification> notifications = notificationRepository.findByUserId(user.getId());
        for (Notification n : notifications) {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        }
        return notificationRepository.saveAll(notifications);
    }

    //Delete single notification
    public void deleteNotification(String id, String userEmail) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!notification.getUserId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this notification");
        }
        notificationRepository.delete(notification);
    }

    // Delete all notifications for a user
    public void deleteAllNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Notification> notifications = notificationRepository.findByUserId(user.getId());
        notificationRepository.deleteAll(notifications);
    }

    //Create notification by userId
    public Notification createNotification(String userId, String type, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return saveAndSendNotification(user, type, message);
    }

    // Create notification using email
    public Notification createNotificationForEmail(String email, String type, String message) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return saveAndSendNotification(user, type, message);
    }

    // Common method for saving and sending email
    private Notification saveAndSendNotification(User user, String type, String message) {
        Notification notification = new Notification();
        notification.setUserId(user.getId());
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        // Send email
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setSubject("EdTech Notification: " + type);
        mail.setText(message);
        mailSender.send(mail);

        return notification;
    }

    public Notification createNotificationForGradeEmail(String submissionId, String type, String message) {
        Submission submission=submissionRepository.findById(submissionId).orElseThrow(()->new RuntimeException("not found submissions"));

        User user = userRepository.findById(submission.getStudentId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return saveAndSendNotification(user, type, message);
    }
}
