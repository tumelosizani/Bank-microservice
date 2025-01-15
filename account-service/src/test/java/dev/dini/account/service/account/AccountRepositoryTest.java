package dev.dini.account.service.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
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
        Account account = new Account();
        account.setAccountId(1);
        when(accountRepository.findByAccountId(anyInt())).thenReturn(Optional.of(account));

        Optional<Account> result = accountRepository.findByAccountId(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getAccountId());
    }

    @Test
    void findByAccountId_withInvalidId_returnsEmpty() {
        when(accountRepository.findByAccountId(anyInt())).thenReturn(Optional.empty());

        Optional<Account> result = accountRepository.findByAccountId(999);

        assertTrue(result.isEmpty());
    }
}