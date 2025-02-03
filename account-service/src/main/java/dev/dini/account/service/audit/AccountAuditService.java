package dev.dini.account.service.audit;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class AccountAuditService {
    private static final Logger logger = LoggerFactory.getLogger(AccountAuditService.class);

    public void logAccountEvent(UUID accountId, String event, String details) {
        logger.info("Account Event - AccountID: {}, Event: {}, Details: {}, Timestamp: {}",
                accountId, event, details, LocalDateTime.now());
    }
}
