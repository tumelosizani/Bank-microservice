package dev.dini.account.service.notification;

import dev.dini.account.service.account.Account;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountNotificationService {

    public void sendNotification(UUID accountId, String message) {
        // Here, you could expand this logic to integrate with a messaging system (email, SMS, etc.)
        System.out.println("Sending notification: " + message);
    }

    public void sendAccountCreationNotification(@NotNull UUID customerId, UUID accountId) {
        sendNotification(accountId, "Account created for customer: " + customerId + ", Account ID: " + accountId);
    }

    public void sendAccountClosureNotification(@NotNull UUID customerId, UUID accountId) {
        sendNotification(accountId, "Account closed for customer: " + customerId + ", Account ID: " + accountId);
    }

    public void sendInterestAppliedNotification(Account account, BigDecimal interest) {
        // Correctly use account.getAccountId() instead of accountId
        sendNotification(account.getAccountId(), "Interest of " + interest + " applied to account: " + account.getAccountId());
    }
}
