package dev.dini.account.service.transaction;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.audit.AccountAuditService;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.security.AccountSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionFeignClient transactionFeignClient;

    @Mock
    private AccountAuditService accountAuditService;

    @Mock
    private AccountSecurityService accountSecurityService;

    @InjectMocks
    private TransactionService transactionService;

    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fromAccountId = UUID.randomUUID();
        toAccountId = UUID.randomUUID();
        amount = BigDecimal.valueOf(100);
        fromAccount = new Account();
        fromAccount.setAccountId(fromAccountId);
        fromAccount.setBalance(BigDecimal.valueOf(200));
        toAccount = new Account();
        toAccount.setAccountId(toAccountId);
        toAccount.setBalance(BigDecimal.valueOf(50));
    }

    @Test
    void transferFundsSuccessfully() {
        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        transactionService.transferFunds(fromAccountId, toAccountId, amount);

        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);
        verify(transactionFeignClient).createTransaction(any(TransactionDTO.class));
        verify(accountAuditService).logAccountEvent(eq(fromAccountId), eq("PROCESS_TRANSACTION"), eq("Transaction processed successfully"));
    }

    @Test
    void transferFundsThrowsAccountNotFoundException() {
        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.transferFunds(fromAccountId, toAccountId, amount));
    }

    @Test
    void transferFundsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.transferFunds(null, toAccountId, amount));
        assertThrows(IllegalArgumentException.class, () -> transactionService.transferFunds(fromAccountId, null, amount));
        assertThrows(IllegalArgumentException.class, () -> transactionService.transferFunds(fromAccountId, toAccountId, null));
    }

    @Test
    void transferFundsThrowsIllegalStateException() {
        fromAccount.setBalance(BigDecimal.valueOf(50));
        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        assertThrows(IllegalStateException.class, () -> transactionService.transferFunds(fromAccountId, toAccountId, amount));
    }
}