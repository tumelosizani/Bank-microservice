package dev.dini.notification.service.dto;

import dev.dini.notification.service.notification.NotificationStatus;
import dev.dini.notification.service.notification.NotificationType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Data
@Getter
@Setter
public class NotificationEvent {

    private String id; // Unique identifier for the event
    private String recipient; // Email or phone number of the recipient
    private NotificationType type; // Type of notification: EMAIL, SMS, PUSH
    private String message; // The content of the notification
    private LocalDateTime timestamp; // When the event was generated
    private NotificationStatus status; // Event status: PENDING, SENT, FAILED
    private String errorDetails;

}