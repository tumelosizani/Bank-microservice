package dev.dini.account.service.exception;

import java.util.UUID;

public class AccountInactiveException extends IllegalStateException {
    public AccountInactiveException(UUID accountId) {
        super("Account ID: " + accountId + " is not active.");
    }
}
