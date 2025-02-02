package dev.dini.account.service.exception;

import java.io.Serializable;
import java.util.UUID;

public class AccountNotFoundException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String errorCode = "ACCOUNT_NOT_FOUND";

    // Constructor with accountId
    public AccountNotFoundException(UUID accountId) {
        super("Account not found with ID: " + accountId);
    }

    // Constructor with accountId and cause
    public AccountNotFoundException(UUID accountId, Throwable cause) {
        super("Account not found with ID: " + accountId, cause);
    }

    // Get error code (optional)
    public String getErrorCode() {
        return errorCode;
    }
}
