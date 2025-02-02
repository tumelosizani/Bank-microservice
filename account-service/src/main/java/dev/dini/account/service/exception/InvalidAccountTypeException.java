package dev.dini.account.service.exception;

import dev.dini.account.service.account.AccountType;

import java.util.UUID;

public class InvalidAccountTypeException extends IllegalArgumentException {
    public InvalidAccountTypeException(UUID accountId, AccountType accountType) {
        super("Account ID: " + accountId + " is not of the expected type: " + accountType);
    }
}
