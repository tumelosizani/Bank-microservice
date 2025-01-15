package dev.dini.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.account.AccountService;
import dev.dini.account.service.account.AccountType;
import dev.dini.account.service.dto.CreateAccountRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AccountServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Test
    public void testCreateAccount() throws Exception {
        // Arrange: Create the request body for the new account
        CreateAccountRequestDTO request = new CreateAccountRequestDTO();
        request.setUserId(12345); // Ensuring userId is an Integer
        request.setAccountType(AccountType.SAVINGS);

        // Act: Send POST request to create a new account
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated()) // Ensure the status is 201 Created
                .andExpect(jsonPath("$.userId").value(12345))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.balance").value(0));

        // Assert: Verify the account is saved in the database
        Account savedAccount = accountRepository.findAll().get(0);
        assertEquals(Integer.valueOf(12345), savedAccount.getCustomerId());
        assertEquals(AccountType.SAVINGS, savedAccount.getAccountType());
        assertEquals(BigDecimal.ZERO, savedAccount.getBalance());
    }

    @Test
    public void testGetAccount() throws Exception {
        // Arrange: Create and save an account
        Account account = new Account();
        account.setCustomerId(12345); // Ensuring userId is an Integer
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.ZERO);
        accountRepository.save(account);

        // Act: Send GET request to fetch the account by userId
        mockMvc.perform(get("/accounts/{userId}", account.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(12345))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    public void testGetAllAccounts() throws Exception {
        // Arrange: Create and save two accounts
        Account account1 = new Account();
        account1.setCustomerId(12345); // Ensuring userId is an Integer
        account1.setAccountType(AccountType.SAVINGS);
        account1.setBalance(BigDecimal.ZERO);
        accountRepository.save(account1);

        Account account2 = new Account();
        account2.setCustomerId(67890); // Ensuring userId is an Integer
        account2.setAccountType(AccountType.CHECKING);
        account2.setBalance(BigDecimal.ZERO);
        accountRepository.save(account2);

        // Act: Send GET request to fetch all accounts
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(12345))
                .andExpect(jsonPath("$[1].userId").value(67890));
    }

    @Test
    public void testCreateAccountWithInvalidData() throws Exception {
        // Arrange: Create a request body with invalid data (e.g., missing userId or invalid accountType)
        CreateAccountRequestDTO request = new CreateAccountRequestDTO();
        request.setUserId(null); // Invalid userId
        request.setAccountType(null); // Invalid accountType

        // Act: Send POST request with invalid data
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Ensure the status is 400 Bad Request
    }

    @Test
    public void testGetAccountNotFound() throws Exception {
        // Act: Send GET request for an account that doesn't exist
        mockMvc.perform(get("/accounts/{userId}", 99999))
                .andExpect(status().isNotFound()); // Ensure the status is 404 Not Found
    }
}
