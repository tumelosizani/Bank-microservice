package dev.dini.account.service.security;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.account.AccountStatus;
import dev.dini.account.service.audit.AccountAuditService;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.notification.AccountNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountSecurityServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountAuditService accountAuditService;

    @Mock
    private AccountNotificationService accountNotificationService;

    @InjectMocks
    private AccountSecurityService accountSecurityService;

    private UUID accountId;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();
        account = new Account();
        account.setAccountId(accountId);
        account.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void freezeAccountSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountSecurityService.freezeAccount(accountId);

        verify(accountRepository).save(account);
        verify(accountAuditService).logAccountEvent(eq(accountId), eq("Account Status"), eq("Frozen"));
        verify(accountNotificationService).sendNotification(eq(accountId), eq("Account Frozen"));
        assertEquals(AccountStatus.FROZEN, account.getStatus());
    }

    @Test
    void freezeAccountThrowsAccountNotFoundException() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountSecurityService.freezeAccount(accountId));
    }

    @Test
    void unfreezeAccountSuccessfully() {
        account.setStatus(AccountStatus.FROZEN);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountSecurityService.unfreezeAccount(accountId);

        verify(accountRepository).save(account);
        verify(accountAuditService).logAccountEvent(eq(accountId), eq("Account Status"), eq("Unfrozen"));
        verify(accountNotificationService).sendNotification(eq(accountId), eq("Account Unfrozen"));
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void unfreezeAccountThrowsAccountNotFoundException() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountSecurityService.unfreezeAccount(accountId));
    }

    @Test
    void setTransactionLimitSuccessfully() {
        BigDecimal limit = BigDecimal.valueOf(1000);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountSecurityService.setTransactionLimit(accountId, limit);

        verify(accountRepository).save(account);
        verify(accountAuditService).logAccountEvent(eq(accountId), eq("Transaction Limit"), eq("Set to: " + limit));
        verify(accountNotificationService).sendNotification(eq(accountId), eq("Transaction Limit Set to: " + limit));
        assertEquals(limit, account.getTransactionLimit());
    }

    @Test
    void setTransactionLimitThrowsAccountNotFoundException() {
        BigDecimal limit = BigDecimal.valueOf(1000);
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountSecurityService.setTransactionLimit(accountId, limit));
    }

    @Test
    void setOverdraftProtectionSuccessfully() {
        boolean enabled = true;
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountSecurityService.setOverdraftProtection(accountId, enabled);

        verify(accountRepository).save(account);
        verify(accountAuditService).logAccountEvent(eq(accountId), eq("Overdraft Protection"), eq("Enabled: " + enabled));
        verify(accountNotificationService).sendNotification(eq(accountId), eq("Overdraft Protection Enabled"));
        assertTrue(account.isOverdraftProtection());
    }

    @Test
    void setOverdraftProtectionThrowsAccountNotFoundException() {
        boolean enabled = true;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountSecurityService.setOverdraftProtection(accountId, enabled));
    }

    @Test
    void closeAccountSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountSecurityService.closeAccount(accountId);

        verify(accountRepository).save(account);
        verify(accountAuditService).logAccountEvent(eq(accountId), eq("Account Status"), eq("Closed"));
        verify(accountNotificationService).sendNotification(eq(accountId), eq("Account Closed"));
        assertEquals(AccountStatus.CLOSED, account.getStatus());
    }

    @Test
    void closeAccountThrowsAccountNotFoundException() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountSecurityService.closeAccount(accountId));
    }

    @Test
    void isAccountLockedReturnsTrueWhenAccountIsFrozen() {
        account.setStatus(AccountStatus.FROZEN);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertTrue(accountSecurityService.isAccountLocked(accountId));
    }

    @Test
    void isAccountLockedReturnsFalseWhenAccountIsActive() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertFalse(accountSecurityService.isAccountLocked(accountId));
    }

    @Test
    void lockAccountSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountSecurityService.lockAccount(accountId);

        verify(accountRepository).save(account);
        assertEquals(AccountStatus.FROZEN, account.getStatus());
    }

    @Test
    void unlockAccountSuccessfully() {
        account.setStatus(AccountStatus.FROZEN);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountSecurityService.unlockAccount(accountId);

        verify(accountRepository).save(account);
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }
}