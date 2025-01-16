package dev.dini.transaction.service.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountServiceClientTest {

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private AccountServiceClientTest accountServiceClientTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAccountId_returnsAccountDTOForValidAccountId() {
        AccountDTO expectedAccount = new AccountDTO();
        expectedAccount.setAccountId(1);
        expectedAccount.setBalance(BigDecimal.valueOf(1000));

        when(accountServiceClient.getAccountId(1)).thenReturn(expectedAccount);

        AccountDTO actualAccount = accountServiceClient.getAccountId(1);

        assertEquals(expectedAccount, actualAccount);
    }

    @Test
    void getAccountId_returnsNullForNonExistentAccountId() {
        when(accountServiceClient.getAccountId(999)).thenReturn(null);

        AccountDTO actualAccount = accountServiceClient.getAccountId(999);

        assertEquals(null, actualAccount);
    }

    @Test
    void deductFunds_deductsAmountFromAccount() {
        doNothing().when(accountServiceClient).deductFunds(1, BigDecimal.valueOf(100));

        accountServiceClient.deductFunds(1, BigDecimal.valueOf(100));

        verify(accountServiceClient, times(1)).deductFunds(1, BigDecimal.valueOf(100));
    }

    @Test
    void addFunds_addsAmountToAccount() {
        doNothing().when(accountServiceClient).addFunds(2, BigDecimal.valueOf(200));

        accountServiceClient.addFunds(2, BigDecimal.valueOf(200));

        verify(accountServiceClient, times(1)).addFunds(2, BigDecimal.valueOf(200));
    }

    @Test
    void getAccountById_returnsAccountDTOForValidSenderAccountId() {
        AccountDTO expectedAccount = new AccountDTO();
        expectedAccount.setAccountId(1);
        expectedAccount.setBalance(BigDecimal.valueOf(1000));

        when(accountServiceClient.getAccountById(1)).thenReturn(expectedAccount);

        AccountDTO actualAccount = accountServiceClient.getAccountById(1);

        assertEquals(expectedAccount, actualAccount);
    }

    @Test
    void getAccountById_returnsNullForNonExistentSenderAccountId() {
        when(accountServiceClient.getAccountById(999)).thenReturn(null);

        AccountDTO actualAccount = accountServiceClient.getAccountById(999);

        assertEquals(null, actualAccount);
    }
}