package dev.dini.notification.service.notification;

import dev.dini.notification.service.dto.NotificationEvent;
import dev.dini.notification.service.dto.NotificationRequest;
import dev.dini.notification.service.email.EmailService;
import dev.dini.notification.service.sms.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Override
    public void sendNotification(NotificationRequest request) {
        NotificationEvent event = NotificationEvent.builder()
                .id(UUID.randomUUID().toString())
                .recipient(request.getRecipient())
                .type(request.getType())
                .message(request.getMessage())
                .timestamp(LocalDateTime.now())
                .status(NotificationStatus.PENDING)
                .build();

        kafkaTemplate.send("notification-events", event);
    }

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void processNotification(NotificationEvent event) {
        try {
            switch (event.getType()) {
                case EMAIL:
                    emailService.sendEmail(event.getRecipient(), "Notification", event.getMessage());
                    break;
                case SMS:
                    smsService.sendSms(event.getRecipient(), "YourTwilioPhoneNumber", event.getMessage());
                    break;
            }
            event.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            event.setStatus(NotificationStatus.FAILED);
        }

        // Convert NotificationEvent to NotificationLog before saving
        NotificationLog log = NotificationLog.builder()
                .id(event.getId())
                .recipient(event.getRecipient())
                .type(event.getType())
                .message(event.getMessage())
                .status(event.getStatus())
                .timestamp(event.getTimestamp())
                .build();

        notificationLogRepository.save(log); // Now, saving the correct entity type
    }



    @Override
    public void scheduleNotification(NotificationRequest request, LocalDateTime scheduleTime) {
        // Implement scheduling logic here
    }

    @Override
    public List<NotificationLog> getNotificationLogs() {
        return notificationLogRepository.findAll();
    }
}