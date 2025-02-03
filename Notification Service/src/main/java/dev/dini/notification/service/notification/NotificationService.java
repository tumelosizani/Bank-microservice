package dev.dini.notification.service.notification;

import dev.dini.notification.service.dto.NotificationRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    void sendNotification(NotificationRequest request);

    void scheduleNotification(NotificationRequest request, LocalDateTime scheduleTime);

    List<NotificationLog> getNotificationLogs();
}
