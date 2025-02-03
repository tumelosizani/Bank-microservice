package dev.dini.account.service.component;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.account.AccountStatus;
import dev.dini.account.service.account.AccountType;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.exception.AccountInactiveException;
import dev.dini.account.service.exception.InvalidAccountTypeException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountVerification {

    private static final Logger logger = LoggerFactory.getLogger(AccountVerification.class);

    private final AccountRepository accountRepository;

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    // Verifying account existence
    public boolean isAccountValid(UUID accountId) {
        return accountRepository.existsById(accountId);
    }

    public void verifyAccountExists(UUID accountId) {
        logger.info("Verifying account exists: {}", accountId);
        getAccount(accountId); // Reuse the getAccount method for exception throwing
    }

    // Verifying account-type
    public boolean isAccountTypeValid(UUID accountId, AccountType accountType) {
        Account account = getAccount(accountId); // Fetch the account once and use it
        return account.getAccountType().equals(accountType);
    }

    public void verifyAccountType(UUID accountId, AccountType accountType) {
        if (!isAccountTypeValid(accountId, accountType)) {
            logger.error("Account ID: {} is not of type: {}", accountId, accountType);
            throw new InvalidAccountTypeException(accountId, accountType);
        }
    }

    // Verifying account status (Active)
    public boolean isAccountActive(UUID accountId) {
        Account account = getAccount(accountId); // Fetch the account once and use it
        return account.getStatus() == AccountStatus.ACTIVE;
    }

    public void verifyAccountActive(UUID accountId) {
        if (!isAccountActive(accountId)) {
            logger.error("Account ID: {} is not active", accountId);
            throw new AccountInactiveException(accountId);
        }
    }
}
