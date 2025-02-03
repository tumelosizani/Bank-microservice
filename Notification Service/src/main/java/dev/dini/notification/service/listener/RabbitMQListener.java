package dev.dini.notification.service.listener;

import dev.dini.notification.service.dto.NotificationRequest;
import dev.dini.notification.service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class RabbitMQListener {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RabbitMQListener.class);

    private final NotificationService notificationService;

    public RabbitMQListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Listens for messages from the notification queue.
     * @param request The incoming notification request message.
     */
    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void receiveMessage(NotificationRequest request) {
        log.info("Received message from RabbitMQ: {}", request);

        try {
            notificationService.sendNotification(request);
            log.info("Notification successfully processed for recipient: {}", request.getRecipient());
        } catch (Exception e) {
            log.error("Failed to process notification for recipient: {}", request.getRecipient(), e);
        }
    }
}
