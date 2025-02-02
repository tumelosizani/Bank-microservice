package dev.dini.account.service.security;

import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.audit.AccountAuditService;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountStatus;
import dev.dini.account.service.notification.AccountNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountSecurityService {

    private final AccountRepository accountRepository;
    private final AccountAuditService accountAuditService;
    private final AccountNotificationService accountNotificationService;

    public void freezeAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
        accountAuditService.logAccountEvent(accountId, "Account Status", "Frozen");
        accountNotificationService.sendNotification(accountId, "Account Frozen");
    }

    public void unfreezeAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        accountAuditService.logAccountEvent(accountId, "Account Status", "Unfrozen");
        accountNotificationService.sendNotification(accountId, "Account Unfrozen");
    }

    public void setTransactionLimit(UUID accountId, BigDecimal limit) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setTransactionLimit(limit);
        accountRepository.save(account);
        accountAuditService.logAccountEvent(accountId, "Transaction Limit", "Set to: " + limit);
        accountNotificationService.sendNotification(accountId, "Transaction Limit Set to: " + limit);
    }

    public void setOverdraftProtection(UUID accountId, boolean enabled) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setOverdraftProtection(enabled);
        accountRepository.save(account);
        accountAuditService.logAccountEvent(accountId, "Overdraft Protection", "Enabled: " + enabled);
        accountNotificationService.sendNotification(accountId, "Overdraft Protection " + (enabled ? "Enabled" : "Disabled"));
    }

    public void closeAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
        accountAuditService.logAccountEvent(accountId, "Account Status", "Closed");
        accountNotificationService.sendNotification(accountId, "Account Closed");
    }

    private Account findAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
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

}
