package dev.dini.notification.service.service;

import dev.dini.notification.service.dto.NotificationRequest;

import java.time.LocalDateTime;

public interface NotificationService {
    void sendNotification(NotificationRequest request);

    void scheduleNotification(NotificationRequest request, LocalDateTime scheduleTime);
}
