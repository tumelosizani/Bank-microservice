package dev.dini.account.service.component;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountNotification {

    public void sendNotification(String message) {
        System.out.println("Sending notification: " + message);
    }

    public void sendAccountCreationNotification(@NotNull UUID customerId, UUID accountId) {
        sendNotification("Account created for customer: " + customerId + ", Account ID: " + accountId);
    }

    public void sendAccountClosureNotification(@NotNull UUID customerId, UUID accountId) {
        sendNotification("Account closed for customer: " + customerId + ", Account ID: " + accountId);
    }
}
