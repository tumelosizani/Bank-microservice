package dev.dini.notification.service.notification;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "notification_logs")
public class NotificationLog {
    @Id
    private String id;
    private String recipient;
    private NotificationType type;
    private String message;
    private String status;
    private LocalDateTime timestamp;
    private String errorDetails;

    public NotificationLog(String recipient, NotificationType type, String message, String status, LocalDateTime timestamp, String errorDetails) {
        this.recipient = recipient;
        this.type = type;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.errorDetails = errorDetails;
    }
}
