package dev.dini.account_service.account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Endpoint to create a new account
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account createdAccount = accountService.createAccount(request.getUserId(), request.getAccountType());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    // Endpoint to retrieve account details
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Integer accountId) {
        Account account = accountService.getAccount(accountId);
        return ResponseEntity.ok(account);
    }

    // Endpoint to update account details
    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable Integer accountId, @RequestBody Account updatedAccount) {
        Account account = accountService.updateAccount(accountId, updatedAccount);
        return ResponseEntity.ok(account);
    }

    // Endpoint to retrieve account balance
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Integer accountId) {
        BigDecimal balance = accountService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    // Endpoint to close an account
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> closeAccount(@PathVariable Integer accountId) {
        accountService.closeAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}

