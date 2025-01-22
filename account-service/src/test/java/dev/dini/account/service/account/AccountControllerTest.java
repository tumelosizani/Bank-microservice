package dev.dini.account.service.account;

import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.AccountResponseDTO;
import dev.dini.account.service.dto.CreateAccountRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void createAccount_withValidData_returnsCreatedAccount() {
        CreateAccountRequestDTO requestDTO = new CreateAccountRequestDTO();
        Account account = new Account();
        when(accountService.createAccount(any(CreateAccountRequestDTO.class))).thenReturn(account);

        ResponseEntity<Account> response = accountController.createAccount(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(account, response.getBody());
    }

    @Test
    void getAccount_withValidId_returnsAccount() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        when(accountService.getAccount(accountId)).thenReturn(account);

        ResponseEntity<Account> response = accountController.getAccount(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(account, response.getBody());
    }

    @Test
    void updateAccount_withValidData_returnsUpdatedAccount() {
        UUID accountId = UUID.randomUUID();
        AccountRequestDTO requestDTO = new AccountRequestDTO();
        AccountResponseDTO responseDTO = new AccountResponseDTO();
        when(accountService.updateAccount(accountId, requestDTO)).thenReturn(responseDTO);

        ResponseEntity<AccountResponseDTO> response = accountController.updateAccount(accountId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void closeAccount_withValidId_returnsNoContent() {
        UUID accountId = UUID.randomUUID();

        ResponseEntity<Void> response = accountController.closeAccount(accountId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getBalance_withValidId_returnsBalance() {
        UUID accountId = UUID.randomUUID();
        BigDecimal balance = BigDecimal.TEN;
        when(accountService.getBalance(accountId)).thenReturn(balance);

        ResponseEntity<BigDecimal> response = accountController.getBalance(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(balance, response.getBody());
    }

    @Test
    void transferFunds_withValidData_returnsOk() {
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;

        ResponseEntity<Void> response = accountController.transferFunds(fromAccountId, toAccountId, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeAccountType_withValidData_returnsUpdatedAccount() {
        UUID accountId = UUID.randomUUID();
        AccountType newAccountType = AccountType.SAVINGS;
        Account account = new Account();
        when(accountService.changeAccountType(accountId, newAccountType)).thenReturn(account);

        ResponseEntity<Account> response = accountController.changeAccountType(accountId, newAccountType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(account, response.getBody());
    }

    @Test
    void setOverdraftProtection_withValidData_returnsOk() {
        UUID accountId = UUID.randomUUID();
        boolean enabled = true;

        ResponseEntity<Void> response = accountController.setOverdraftProtection(accountId, enabled);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void freezeAccount_withValidId_returnsOk() {
        UUID accountId = UUID.randomUUID();

        ResponseEntity<Void> response = accountController.freezeAccount(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void unfreezeAccount_withValidId_returnsOk() {
        UUID accountId = UUID.randomUUID();

        ResponseEntity<Void> response = accountController.unfreezeAccount(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addAccountHolder_withValidData_returnsOk() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        ResponseEntity<Void> response = accountController.addAccountHolder(accountId, customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void removeAccountHolder_withValidData_returnsOk() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        ResponseEntity<Void> response = accountController.removeAccountHolder(accountId, customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void checkAccountStatus_withValidId_returnsStatus() {
        UUID accountId = UUID.randomUUID();
        AccountStatus status = AccountStatus.ACTIVE;
        when(accountService.checkAccountStatus(accountId)).thenReturn(status);

        ResponseEntity<AccountStatus> response = accountController.checkAccountStatus(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(status, response.getBody());
    }

    @Test
    void setTransactionLimit_withValidData_returnsOk() {
        UUID accountId = UUID.randomUUID();
        BigDecimal limit = BigDecimal.TEN;

        ResponseEntity<Void> response = accountController.setTransactionLimit(accountId, limit);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}