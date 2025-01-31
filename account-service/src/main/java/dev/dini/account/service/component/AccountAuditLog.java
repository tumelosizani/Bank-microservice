package dev.dini.account.service.component;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class AccountAuditLog {

    private static final Logger logger = LoggerFactory.getLogger(AccountAuditLog.class);

    public void logAccountEvent(UUID accountId, String event, String details) {
        logger.info("Account Event - AccountID: {}, Event: {}, Details: {}, Timestamp: {}",
                accountId, event, details, LocalDateTime.now());
    }
}
