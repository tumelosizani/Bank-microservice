package dev.dini.account.service.exception;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class AccountNotFoundException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Get error code (optional)
    private final String errorCode = "ACCOUNT_NOT_FOUND";

    // Constructor with accountId
    public AccountNotFoundException(UUID accountId) {
        super("Account not found with ID: " + accountId);
    }

    // Constructor with accountId and cause
    public AccountNotFoundException(UUID accountId, Throwable cause) {
        super("Account not found with ID: " + accountId, cause);
    }

}
