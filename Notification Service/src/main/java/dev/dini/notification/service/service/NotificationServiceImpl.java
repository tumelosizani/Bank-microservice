package dev.dini.notification.service.service;

import dev.dini.notification.service.dto.NotificationRequest;
import dev.dini.notification.service.notification.NotificationLog;
import dev.dini.notification.service.notification.NotificationLogRepository;
import dev.dini.notification.service.notification.NotificationType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationLogRepository notificationLogRepository;
    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    public NotificationServiceImpl(EmailService emailService, SmsService smsService, NotificationLogRepository notificationLogRepository) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.notificationLogRepository = notificationLogRepository;
    }

    @Override
    public void sendNotification(NotificationRequest request) {
        String status = "PENDING";
        String errorDetails = null;

        try {
            status = switch (request.getType()) {
                case NotificationType.EMAIL -> {
                    emailService.sendEmail(request.getRecipient(), request.getMessage());
                    yield "SENT";
                }
                case NotificationType.SMS -> {
                    smsService.sendSms(request.getRecipient(), request.getMessage());
                    yield "SENT";
                }
                default -> throw new IllegalArgumentException("Unsupported notification type: " + request.getType());
            };
        } catch (Exception exception) {
            status = "FAILED";
            errorDetails = exception.getMessage();
        }

        NotificationLog log = new NotificationLog(
                request.getRecipient(),
                request.getType(),
                request.getMessage(),
                status,
                LocalDateTime.now(),
                errorDetails
        );
        notificationLogRepository.save(log);
    }

    @Override
    public void scheduleNotification(NotificationRequest request, LocalDateTime scheduleTime) {
        taskScheduler.initialize();
        taskScheduler.schedule(() -> sendNotification(request), Instant.from(scheduleTime));
    }
}