package dev.dini.account_service.account;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(Integer userId, AccountType accountType) {
        Account newAccount = new Account();
        newAccount.setAccountId(UUID.randomUUID().hashCode());
        newAccount.setUserId(userId);
        newAccount.setAccountType(accountType);
        newAccount.setBalance(BigDecimal.ZERO); // Initial balance is zero
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(newAccount);
    }


    // Retrieve account details
    @Override
    public Account getAccount(Integer accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    // Close account
    @Override
    public void closeAccount(Integer accountId) {
        Account existingAccount = getAccount(accountId);
        accountRepository.delete(existingAccount);
    }

    // Get account balance
    @Override
    public BigDecimal getBalance(Integer accountId) {
        Account account = getAccount(accountId);
        return account.getBalance();
    }

    // Update account details
    @Override
    public Account updateAccount(Integer accountId, Account updatedAccount) {
        return null;
    }
}
