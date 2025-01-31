package dev.dini.account.service.account;

import dev.dini.account.service.customer.CustomerServiceClient;
import dev.dini.account.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    // Create a new account
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequestDTO createAccountRequestDTO) {
        logger.info("Creating a new account with request: {}", createAccountRequestDTO);

        Account createdAccount;
        try {
            createdAccount = accountService.createAccount(createAccountRequestDTO);
        } catch (Exception e) {
            logger.error("Error creating account", e);
            return ResponseEntity.status(500).build();
        }

        logger.info("Successfully created account: {}", createdAccount);
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable UUID accountId) {
        Account account = accountService.getAccount(accountId);
        return ResponseEntity.ok(account);
    }

    // Retrieve all accounts linked to a specific customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable UUID customerId) {
        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable UUID accountId,
                                                            @RequestBody AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO updatedAccount = accountService.updateAccount(accountId, accountRequestDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> closeAccount(@PathVariable UUID accountId) {
        accountService.closeAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID accountId) {
        BigDecimal balance = accountService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferFunds(@RequestParam UUID fromAccountId,
                                              @RequestParam UUID toAccountId,
                                              @RequestParam BigDecimal amount) {
        accountService.transferFunds(fromAccountId, toAccountId, amount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/accountType")
    public ResponseEntity<Account> changeAccountType(@PathVariable UUID accountId,
                                                     @RequestBody AccountType newAccountType) {
        Account updatedAccount = accountService.changeAccountType(accountId, newAccountType);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/{accountId}/overdraftProtection")
    public ResponseEntity<Void> setOverdraftProtection(@PathVariable UUID accountId,
                                                       @RequestParam boolean enabled) {
        accountService.setOverdraftProtection(accountId, enabled);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/freeze")
    public ResponseEntity<Void> freezeAccount(@PathVariable UUID accountId) {
        accountService.freezeAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/unfreeze")
    public ResponseEntity<Void> unfreezeAccount(@PathVariable UUID accountId) {
        accountService.unfreezeAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/addHolder")
    public ResponseEntity<Void> addAccountHolder(@PathVariable UUID accountId,
                                                 @RequestParam UUID customerId) {
        accountService.addAccountHolder(accountId, customerId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/removeHolder")
    public ResponseEntity<Void> removeAccountHolder(@PathVariable UUID accountId,
                                                    @RequestParam UUID customerId) {
        accountService.removeAccountHolder(accountId, customerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountId}/status")
    public ResponseEntity<AccountStatus> checkAccountStatus(@PathVariable UUID accountId) {
        AccountStatus status = accountService.checkAccountStatus(accountId);
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{accountId}/transactionLimit")
    public ResponseEntity<Void> setTransactionLimit(@PathVariable UUID accountId,
                                                    @RequestParam BigDecimal limit) {
        accountService.setTransactionLimit(accountId, limit);
        return ResponseEntity.ok().build();
    }

    // Add the linkAccountToCustomer endpoint
    @PutMapping("/{accountId}/linkCustomer")
    public ResponseEntity<Void> linkAccountToCustomer(@PathVariable UUID accountId,
                                                      @RequestParam UUID customerId) {
        accountService.linkAccountToCustomer(accountId, customerId);
        return ResponseEntity.ok().build();
    }

    // Add the calculateInterest endpoint
    @PostMapping("/calculateInterest")
    public ResponseEntity<InterestCalculationResponseDTO> calculateInterest(@RequestBody InterestCalculationRequestDTO request) {
        logger.info("Calculating interest for account ID: {}", request.getAccountId());
        InterestCalculationResponseDTO response = accountService.calculateInterest(request);
        return ResponseEntity.ok(response);
    }

}