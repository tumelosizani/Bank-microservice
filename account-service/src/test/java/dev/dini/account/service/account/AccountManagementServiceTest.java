package dev.dini.account.service.account;

import dev.dini.account.service.customer.CustomerServiceClient;
import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.CreateAccountRequestDTO;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.security.AccountSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class AccountManagementServiceTest {

    @Mock
    private AccountInfoService accountInfoService;

    @Mock
    private AccountSecurityService accountSecurityService;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @InjectMocks
    private AccountManagementService accountManagementService;

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
    void createAccount_withInvalidCustomerId_throwsException() {
        CreateAccountRequestDTO requestDTO = new CreateAccountRequestDTO();
        requestDTO.setCustomerId(UUID.randomUUID());
        when(customerServiceClient.getCustomerById(any(UUID.class))).thenThrow(new IllegalArgumentException("Invalid customer ID"));

        assertThrows(IllegalArgumentException.class, () -> accountManagementService.createAccount(requestDTO));
    }

    @Test
    void updateAccount_withNonExistentAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        AccountRequestDTO requestDTO = new AccountRequestDTO();
        when(accountInfoService.getAccount(accountId)).thenThrow(new AccountNotFoundException(accountId));

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.updateAccount(accountId, requestDTO));
    }

    @Test
    void closeAccount_withNonExistentAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        doThrow(new AccountNotFoundException(accountId)).when(accountSecurityService).closeAccount(accountId);

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.closeAccount(accountId));
    }

    @Test
    void changeAccountType_withInvalidTypeChange_throwsException() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountType(AccountType.SAVINGS);
        when(accountInfoService.getAccount(accountId)).thenReturn(account);

        assertThrows(IllegalArgumentException.class, () -> accountManagementService.changeAccountType(accountId, AccountType.SAVINGS));
    }

    @Test
    void addAccountHolder_withNonExistentAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(accountInfoService.getAccount(accountId)).thenThrow(new AccountNotFoundException(accountId));

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.addAccountHolder(accountId, customerId));
    }

    @Test
    void removeAccountHolder_withNonExistentAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(accountInfoService.getAccount(accountId)).thenThrow(new AccountNotFoundException(accountId));

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.removeAccountHolder(accountId, customerId));
    }

    @Test
    void linkAccountToCustomer_withNonExistentAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(accountInfoService.getAccount(accountId)).thenThrow(new AccountNotFoundException(accountId));

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.linkAccountToCustomer(accountId, customerId));
    }

    @Test
    void setTransactionLimit_withNonExistentAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        BigDecimal limit = BigDecimal.TEN;
        when(accountInfoService.getAccount(accountId)).thenThrow(new AccountNotFoundException(accountId));

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.setTransactionLimit(accountId, limit));
    }

    @Test
    void setOverdraftProtection_withNonExistentAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        boolean enabled = true;
        when(accountInfoService.getAccount(accountId)).thenThrow(new AccountNotFoundException(accountId));

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.setOverdraftProtection(accountId, enabled));
    }

    @Test
    void freezeAccount_withNonExistentAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        doThrow(new AccountNotFoundException(accountId)).when(accountSecurityService).freezeAccount(accountId);

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.freezeAccount(accountId));
    }

    @Test
    void unfreezeAccount_withNonExistentAccountId_throwsException() {
        UUID accountId = UUID.randomUUID();
        doThrow(new AccountNotFoundException(accountId)).when(accountSecurityService).unfreezeAccount(accountId);

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.unfreezeAccount(accountId));
    }
}