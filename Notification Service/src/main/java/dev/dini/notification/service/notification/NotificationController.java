package dev.dini.notification.service.notification;

import dev.dini.notification.service.dto.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationLogRepository notificationLogRepository;

    public NotificationController(NotificationService notificationService, NotificationLogRepository notificationLogRepository) {
        this.notificationService = notificationService;
        this.notificationLogRepository = notificationLogRepository;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequest request) {
        notificationService.sendNotification(request);
        return "Notification sent!";
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleNotification(@RequestBody NotificationRequest request, @RequestParam LocalDateTime scheduleTime) {
        notificationService.scheduleNotification(request, scheduleTime);
        return ResponseEntity.ok("Notification scheduled successfully");
    }

    @GetMapping("/logs")
    public List<NotificationLog> getNotificationLogs() {
        return notificationLogRepository.findAll();
    }

    @GetMapping("/logs/status/{status}")
    public List<NotificationLog> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        return notificationLogRepository.findByStatus(status);
    }


}
