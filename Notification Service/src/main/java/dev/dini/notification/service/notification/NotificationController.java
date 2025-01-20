package dev.dini.notification.service.notification;

import dev.dini.notification.service.dto.NotificationRequest;
import dev.dini.notification.service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
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
}

