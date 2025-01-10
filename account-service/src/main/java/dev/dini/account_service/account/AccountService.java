package dev.dini.account_service.account;

import java.math.BigDecimal;

public interface AccountService {
    Account createAccount(Integer userId, AccountType accountType);

    // Retrieve account details
    Account getAccount(Integer accountId);

    // Close account
    void closeAccount(Integer accountId);

    // Get account balance
    BigDecimal getBalance(Integer accountId);

    Account updateAccount(Integer accountId, Account updatedAccount);
}
