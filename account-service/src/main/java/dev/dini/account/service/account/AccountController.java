package dev.dini.account.service.account;

import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.AccountResponseDTO;
import dev.dini.account.service.dto.CreateAccountRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequestDTO createAccountRequestDTO) {
        Account createdAccount = accountService.createAccount(createAccountRequestDTO);
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Integer accountId) {
        Account account = accountService.getAccount(accountId);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Integer accountId, @RequestBody AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO updatedAccount = accountService.updateAccount(accountId, accountRequestDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> closeAccount(@PathVariable Integer accountId) {
        accountService.closeAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Integer accountId) {
        BigDecimal balance = accountService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferFunds(@RequestParam Integer fromAccountId, @RequestParam Integer toAccountId, @RequestParam BigDecimal amount) {
        accountService.transferFunds(fromAccountId, toAccountId, amount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/accountType")
    public ResponseEntity<Account> changeAccountType(@PathVariable Integer accountId, @RequestBody AccountType newAccountType) {
        Account updatedAccount = accountService.changeAccountType(accountId, newAccountType);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/{accountId}/overdraftProtection")
    public ResponseEntity<Void> setOverdraftProtection(@PathVariable Integer accountId, @RequestParam boolean enabled) {
        accountService.setOverdraftProtection(accountId, enabled);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/freeze")
    public ResponseEntity<Void> freezeAccount(@PathVariable Integer accountId) {
        accountService.freezeAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/unfreeze")
    public ResponseEntity<Void> unfreezeAccount(@PathVariable Integer accountId) {
        accountService.unfreezeAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/addHolder")
    public ResponseEntity<Void> addAccountHolder(@PathVariable Integer accountId, @RequestParam Integer customerId) {
        accountService.addAccountHolder(accountId, customerId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/removeHolder")
    public ResponseEntity<Void> removeAccountHolder(@PathVariable Integer accountId, @RequestParam Integer customerId) {
        accountService.removeAccountHolder(accountId, customerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountId}/status")
    public ResponseEntity<AccountStatus> checkAccountStatus(@PathVariable Integer accountId) {
        AccountStatus status = accountService.checkAccountStatus(accountId);
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{accountId}/transactionLimit")
    public ResponseEntity<Void> setTransactionLimit(@PathVariable Integer accountId, @RequestParam BigDecimal limit) {
        accountService.setTransactionLimit(accountId, limit);
        return ResponseEntity.ok().build();
    }
}