package dev.dini.account.service.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AccountRepositoryTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountRepositoryTest accountRepositoryTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByAccountId_withValidId_returnsAccount() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);
        when(accountRepository.findByAccountId(any(UUID.class))).thenReturn(Optional.of(account));

        Optional<Account> result = accountRepository.findByAccountId(accountId);

        assertTrue(result.isPresent());
        assertEquals(accountId, result.get().getAccountId());
    }

    @Test
    void findByAccountId_withInvalidId_returnsEmpty() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findByAccountId(any(UUID.class))).thenReturn(Optional.empty());

        Optional<Account> result = accountRepository.findByAccountId(accountId);

        assertTrue(result.isEmpty());
    }
}