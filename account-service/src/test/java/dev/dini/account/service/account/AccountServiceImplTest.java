package dev.dini.account.service.account;

import dev.dini.account.service.dto.*;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.exception.InsufficientFundsException;
import dev.dini.account.service.interest.InterestCalculationService;
import dev.dini.account.service.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountManagementService accountManagementService;

    @Mock
    private AccountInfoService accountInfoService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private InterestCalculationService interestCalculationService;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void initNewMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccountSuccessfully() {
        // Given
        CreateAccountRequestDTO requestDTO = new CreateAccountRequestDTO();
        Account expectedAccount = new Account();
        when(accountManagementService.createAccount(requestDTO)).thenReturn(expectedAccount);

        // When
        Account result = accountServiceImpl.createAccount(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(expectedAccount, result);
        verify(accountManagementService, times(1)).createAccount(requestDTO);
    }

    @Test
    void getAccountSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        Account expectedAccount = new Account();
        when(accountInfoService.getAccount(accountId)).thenReturn(expectedAccount);

        // When
        Account result = accountServiceImpl.getAccount(accountId);

        // Then
        assertNotNull(result);
        assertEquals(expectedAccount, result);
        verify(accountInfoService, times(1)).getAccount(accountId);
    }

    @Test
    void getAccountNotFound() {
        // Given
        UUID accountId = UUID.randomUUID();
        when(accountInfoService.getAccount(accountId)).thenThrow(new AccountNotFoundException(accountId));

        // When / Then
        assertThrows(AccountNotFoundException.class, () -> accountServiceImpl.getAccount(accountId));
    }

    @Test
    void closeAccountSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        doNothing().when(accountManagementService).closeAccount(accountId);

        // When
        accountServiceImpl.closeAccount(accountId);

        // Then
        verify(accountManagementService, times(1)).closeAccount(accountId);
    }

    @Test
    void closeAccountNotFound() {
        // Given
        UUID accountId = UUID.randomUUID();
        doThrow(new AccountNotFoundException(accountId)).when(accountManagementService).closeAccount(accountId);

        // When / Then
        assertThrows(AccountNotFoundException.class, () -> accountServiceImpl.closeAccount(accountId));
    }

    @Test
    void transferFundsSuccessfully() {
        // Given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        doNothing().when(transactionService).transferFunds(fromAccountId, toAccountId, amount);

        // When
        accountServiceImpl.transferFunds(fromAccountId, toAccountId, amount);

        // Then
        verify(transactionService, times(1)).transferFunds(fromAccountId, toAccountId, amount);
    }

    @Test
    void transferFundsInsufficientFunds() {
        // Given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("200.00");

        doThrow(new InsufficientFundsException(fromAccountId)).when(transactionService).transferFunds(fromAccountId, toAccountId, amount);

        // When / Then
        assertThrows(InsufficientFundsException.class, () -> accountServiceImpl.transferFunds(fromAccountId, toAccountId, amount));
    }

    @Test
    void setOverdraftProtectionSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        boolean enabled = true;
        doNothing().when(accountManagementService).setOverdraftProtection(accountId, enabled);

        // When
        accountServiceImpl.setOverdraftProtection(accountId, enabled);

        // Then
        verify(accountManagementService, times(1)).setOverdraftProtection(accountId, enabled);
    }

    @Test
    void getBalanceSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        BigDecimal expectedBalance = new BigDecimal("123.45");
        when(accountInfoService.getBalance(accountId)).thenReturn(expectedBalance);

        // When
        BigDecimal actualBalance = accountServiceImpl.getBalance(accountId);

        // Then
        assertEquals(expectedBalance, actualBalance);
        verify(accountInfoService, times(1)).getBalance(accountId);
    }

    @Test
    void updateAccountSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        AccountRequestDTO requestDTO = new AccountRequestDTO();
        AccountResponseDTO expectedResponse = new AccountResponseDTO();
        when(accountManagementService.updateAccount(accountId, requestDTO)).thenReturn(expectedResponse);

        // When
        AccountResponseDTO result = accountServiceImpl.updateAccount(accountId, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(accountManagementService, times(1)).updateAccount(accountId, requestDTO);
    }

    @Test
    void changeAccountTypeSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        AccountType newType = AccountType.CHECKING;
        Account expectedAccount = new Account();
        expectedAccount.setAccountType(newType);
        when(accountManagementService.changeAccountType(accountId, newType)).thenReturn(expectedAccount);

        // When
        Account actualAccount = accountServiceImpl.changeAccountType(accountId, newType);

        // Then
        assertEquals(newType, actualAccount.getAccountType());
        verify(accountManagementService, times(1)).changeAccountType(accountId, newType);
    }

    @Test
    void freezeAccountSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();

        // When
        accountServiceImpl.freezeAccount(accountId);

        // Then
        verify(accountManagementService, times(1)).freezeAccount(accountId);
    }

    @Test
    void unfreezeAccountSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();

        // When
        accountServiceImpl.unfreezeAccount(accountId);

        // Then
        verify(accountManagementService, times(1)).unfreezeAccount(accountId);
    }

    @Test
    void addAccountHolderSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        // When
        accountServiceImpl.addAccountHolder(accountId, customerId);

        // Then
        verify(accountManagementService, times(1)).addAccountHolder(accountId, customerId);
    }

    @Test
    void removeAccountHolderSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        // When
        accountServiceImpl.removeAccountHolder(accountId, customerId);

        // Then
        verify(accountManagementService, times(1)).removeAccountHolder(accountId, customerId);
    }

    @Test
    void checkAccountStatusSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        AccountStatus expectedStatus = AccountStatus.ACTIVE;
        when(accountInfoService.checkAccountStatus(accountId)).thenReturn(expectedStatus);

        // When
        AccountStatus actualStatus = accountServiceImpl.checkAccountStatus(accountId);

        // Then
        assertEquals(expectedStatus, actualStatus);
        verify(accountInfoService, times(1)).checkAccountStatus(accountId);
    }

    @Test
    void setTransactionLimitSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        BigDecimal limit = new BigDecimal("5000");

        // When
        accountServiceImpl.setTransactionLimit(accountId, limit);

        // Then
        verify(accountManagementService, times(1)).setTransactionLimit(accountId, limit);
    }

    @Test
    void processTransactionSuccessfully() {
        // Given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000");

        // When
        accountServiceImpl.processTransaction(fromAccountId, toAccountId, amount);

        // Then
        verify(transactionService, times(1)).transferFunds(fromAccountId, toAccountId, amount);
    }

    @Test
    void linkAccountToCustomerSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        // When
        accountServiceImpl.linkAccountToCustomer(accountId, customerId);

        // Then
        verify(accountManagementService, times(1)).linkAccountToCustomer(accountId, customerId);
    }

    @Test
    void getAccountsByCustomerIdSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        List<Account> expectedAccounts = List.of(new Account(), new Account());
        when(accountInfoService.getAccountsByCustomerId(customerId)).thenReturn(expectedAccounts);

        // When
        List<Account> actualAccounts = accountServiceImpl.getAccountsByCustomerId(customerId);

        // Then
        assertEquals(expectedAccounts.size(), actualAccounts.size());
        verify(accountInfoService, times(1)).getAccountsByCustomerId(customerId);
    }

    @Test
    void calculateInterestSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        InterestCalculationRequestDTO request = new InterestCalculationRequestDTO();
        request.setAccountId(accountId);
        InterestCalculationResponseDTO response = new InterestCalculationResponseDTO();
        when(interestCalculationService.calculateInterest(request)).thenReturn(response);

        // When
        InterestCalculationResponseDTO result = accountServiceImpl.calculateInterest(request);

        // Then
        verify(interestCalculationService, times(1)).calculateInterest(request);
        assertEquals(response, result);
    }
}