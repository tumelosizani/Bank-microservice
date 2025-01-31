package dev.dini.account.service.component;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.account.AccountStatus;
import dev.dini.account.service.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountLocking {

    private final AccountRepository accountRepository;

    private Account findAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
    }

    public boolean isAccountLocked(UUID accountId) {
        Account account = findAccountById(accountId);
        return account.getStatus() == AccountStatus.FROZEN;
    }

    public void lockAccount(UUID accountId) {
        Account account = findAccountById(accountId);
        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
    }

    public void unlockAccount(UUID accountId) {
        Account account = findAccountById(accountId);
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    public void freezeAccount(UUID accountId) {
        lockAccount(accountId);
    }

    public void unfreezeAccount(UUID accountId) {
        unlockAccount(accountId);
    }

    public boolean isAccountFrozen(UUID accountId) {
        return isAccountLocked(accountId);
    }

    public boolean isAccountActive(UUID accountId) {
        Account account = findAccountById(accountId);
        return account.getStatus() == AccountStatus.ACTIVE;
    }

    public boolean isAccountClosed(UUID accountId) {
        Account account = findAccountById(accountId);
        return account.getStatus() == AccountStatus.CLOSED;
    }

    public void closeAccount(UUID accountId) {
        Account account = findAccountById(accountId);
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    public Account getAccount(UUID accountId) {
        return findAccountById(accountId);
    }
}
