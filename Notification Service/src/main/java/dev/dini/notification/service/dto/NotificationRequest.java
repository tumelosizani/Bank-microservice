package dev.dini.notification.service.dto;

import dev.dini.notification.service.notification.NotificationType;
import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationRequest {

    private String recipient; // Email or phone number
    private NotificationType type;      // EMAIL or SMS
    private String message;   // Message content

    public String getRecipient() {
        return recipient;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
