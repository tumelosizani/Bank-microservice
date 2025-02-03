package dev.dini.account.service.account;

import dev.dini.account.service.customer.CustomerDTO;
import dev.dini.account.service.customer.CustomerServiceClient;
import dev.dini.account.service.dto.CreateAccountRequestDTO;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.exception.InsufficientFundsException;
import dev.dini.account.service.mapper.AccountMapper;
import dev.dini.account.service.transaction.TransactionDTO;
import dev.dini.account.service.transaction.TransactionFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionFeignClient transactionFeignClient;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void setUp() {
        try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
            // Initialization code
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateAccount() {
        // Given
        UUID customerId = UUID.randomUUID();
        CreateAccountRequestDTO requestDTO = new CreateAccountRequestDTO();
        requestDTO.setCustomerId(customerId);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId(customerId);

        Account account = new Account();
        account.setCustomerId(customerId);
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        when(customerServiceClient.getCustomerById(any(UUID.class))).thenReturn(customerDTO);
        when(accountMapper.toAccountFromCreateRequest(any(CreateAccountRequestDTO.class))).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // When
        Account result = accountServiceImpl.createAccount(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(customerDTO.getCustomerId(), result.getCustomerId());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testGetAccount() {
        // Given
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // When
        Account result = accountServiceImpl.getAccount(accountId);

        // Then
        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
    }

    @Test
    void testGetAccountNotFound() {
        // Given
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(AccountNotFoundException.class, () -> accountServiceImpl.getAccount(accountId));
    }

    @Test
    void testCloseAccount() {
        // Given
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // When
        accountServiceImpl.closeAccount(accountId);

        // Then
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    void testTransferFunds() {
        // Given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        Account fromAccount = new Account();
        fromAccount.setAccountId(fromAccountId);
        fromAccount.setBalance(new BigDecimal("200.00"));

        Account toAccount = new Account();
        toAccount.setAccountId(toAccountId);
        toAccount.setBalance(new BigDecimal("50.00"));

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(fromAccount).thenReturn(toAccount);

        // When
        accountServiceImpl.transferFunds(fromAccountId, toAccountId, amount);

        // Then
        assertEquals(new BigDecimal("100.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("150.00"), toAccount.getBalance());
        verify(accountRepository, times(2)).save(any(Account.class));

        ArgumentCaptor<TransactionDTO> transactionCaptor = ArgumentCaptor.forClass(TransactionDTO.class);
        verify(transactionFeignClient, times(1)).createTransaction(transactionCaptor.capture());

        TransactionDTO transactionDTO = transactionCaptor.getValue();
        assertEquals(fromAccountId, transactionDTO.getFromAccountId());
        assertEquals(toAccountId, transactionDTO.getToAccountId());
        assertEquals(amount, transactionDTO.getAmount());
    }

    @Test
    void testTransferFundsInsufficientFunds() {
        // Given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("200.00");

        Account fromAccount = new Account();
        fromAccount.setAccountId(fromAccountId);
        fromAccount.setBalance(new BigDecimal("100.00"));

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(new Account()));

        // When / Then
        assertThrows(InsufficientFundsException.class, () -> accountServiceImpl.transferFunds(fromAccountId, toAccountId, amount));
    }

    @Test
    void testSetOverdraftProtection() {
        // Given
        UUID accountId = UUID.randomUUID();
        boolean enabled = true;

        Account account = new Account();
        account.setAccountId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // When
        accountServiceImpl.setOverdraftProtection(accountId, enabled);

        // Then
        assertTrue(account.isOverdraftProtection());
        verify(accountRepository, times(1)).save(account);
    }

    // Add more tests for other methods as needed
}