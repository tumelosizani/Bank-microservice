package dev.dini.account.service.account;

import dev.dini.account.service.component.AccountBalanceChecker;
import dev.dini.account.service.customer.CustomerDTO;
import dev.dini.account.service.customer.CustomerServiceClient;
import dev.dini.account.service.dto.AccountResponseDTO;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.mapper.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AccountInfoServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountBalanceChecker accountBalanceChecker;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountInfoService accountInfoService;

    private UUID accountId;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();
        account = new Account();
        account.setAccountId(accountId);
        account.setBalance(BigDecimal.valueOf(1000));
        account.setAccountType(AccountType.SAVINGS);
        account.setStatus(AccountStatus.ACTIVE);
    }


    @Test
    void getAccount_withValidAccountId_returnsAccount() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = accountInfoService.getAccount(accountId);

        assertEquals(account, result);
    }

    @Test
    void getAccount_withInvalidAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountInfoService.getAccount(accountId));
    }

    @Test
    void getBalance_withValidAccountId_returnsBalance() {
        UUID accountId = UUID.randomUUID();
        BigDecimal balance = BigDecimal.valueOf(1000);
        when(accountBalanceChecker.getAccountBalance(accountId)).thenReturn(balance);

        BigDecimal result = accountInfoService.getBalance(accountId);

        assertEquals(balance, result);
    }

    @Test
    void checkAccountStatus_withValidAccountId_returnsStatus() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setStatus(AccountStatus.ACTIVE);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountStatus result = accountInfoService.checkAccountStatus(accountId);

        assertEquals(AccountStatus.ACTIVE, result);
    }

    @Test
    void getAccountsByCustomerId_withValidCustomerId_returnsAccounts() {
        UUID customerId = UUID.randomUUID();
        CustomerDTO customerDTO = new CustomerDTO();
        when(customerServiceClient.getCustomerById(customerId)).thenReturn(customerDTO);
        List<Account> accounts = List.of(new Account());
        when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);

        List<Account> result = accountInfoService.getAccountsByCustomerId(customerId);

        assertEquals(accounts, result);
    }

    @Test
    void getAccountsByCustomerId_withInvalidCustomerId_throwsException() {
        UUID customerId = UUID.randomUUID();
        when(customerServiceClient.getCustomerById(customerId)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> accountInfoService.getAccountsByCustomerId(customerId));
    }

    @Test
    void getAccountResponse_withValidAccountId_returnsAccountResponseDTO() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountResponseDTO(account)).thenReturn(accountResponseDTO);

        AccountResponseDTO result = accountInfoService.getAccountResponse(accountId);

        assertEquals(accountResponseDTO, result);
    }
}
