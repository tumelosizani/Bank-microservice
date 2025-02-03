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
    private NotificationStatus status;
    private LocalDateTime timestamp;
    private String errorDetails;
}
