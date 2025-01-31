package dev.dini.account.service.component;

import dev.dini.account.service.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountVerification {

    private final AccountRepository accountRepository;

    public boolean isAccountValid(UUID accountId) {
        return accountRepository.existsById(accountId);
    }

    public void verifyAccountExists(UUID accountId) {
        if (!isAccountValid(accountId)) {
            throw new IllegalArgumentException("Account does not exist: " + accountId);
        }
    }
}
