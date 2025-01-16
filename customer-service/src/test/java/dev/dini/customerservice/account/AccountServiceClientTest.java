package dev.dini.customerservice.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountServiceClientTest {

    @Mock
    private AccountServiceClient accountServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAccountById_returnsAccount() {
        List<Integer> accountId = List.of(1);
        AccountDTO expectedAccount = new AccountDTO();
        when(accountServiceClient.getAccountById(accountId)).thenReturn(expectedAccount);

        AccountDTO actualAccount = accountServiceClient.getAccountById(accountId);

        assertEquals(expectedAccount, actualAccount);
    }

    @Test
    void deductFunds_executesSuccessfully() {
        Integer senderAccountId = 1;
        BigDecimal amount = BigDecimal.valueOf(100);

        doNothing().when(accountServiceClient).deductFunds(senderAccountId, amount);

        accountServiceClient.deductFunds(senderAccountId, amount);

        verify(accountServiceClient, times(1)).deductFunds(senderAccountId, amount);
    }

    @Test
    void addFunds_executesSuccessfully() {
        Integer receiverAccountId = 1;
        BigDecimal amount = BigDecimal.valueOf(100);

        doNothing().when(accountServiceClient).addFunds(receiverAccountId, amount);

        accountServiceClient.addFunds(receiverAccountId, amount);

        verify(accountServiceClient, times(1)).addFunds(receiverAccountId, amount);
    }

    @Test
    void linkAccountToCustomer_executesSuccessfully() {
        Integer accountId = 1;
        Integer customerId = 1;

        doNothing().when(accountServiceClient).linkAccountToCustomer(accountId, customerId);

        accountServiceClient.linkAccountToCustomer(accountId, customerId);

        verify(accountServiceClient, times(1)).linkAccountToCustomer(accountId, customerId);
    }

    @Test
    void getAccountsByCustomerId_returnsAccounts() {
        Integer customerId = 1;
        List<AccountDTO> expectedAccounts = List.of(new AccountDTO());
        when(accountServiceClient.getAccountsByCustomerId(customerId)).thenReturn(expectedAccounts);

        List<AccountDTO> actualAccounts = accountServiceClient.getAccountsByCustomerId(customerId);

        assertEquals(expectedAccounts, actualAccounts);
    }
}