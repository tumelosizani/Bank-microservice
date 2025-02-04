package dev.dini.account.service.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class InsufficientFundsException extends RuntimeException {
    private final UUID accountId;

    public InsufficientFundsException(UUID accountId) {
        super("Insufficient funds in account with ID: " + accountId);
        this.accountId = accountId;
    }

}