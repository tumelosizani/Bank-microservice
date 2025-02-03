package dev.dini.notification.service.notification;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
    List<NotificationLog> findByStatus(NotificationStatus status);

}
