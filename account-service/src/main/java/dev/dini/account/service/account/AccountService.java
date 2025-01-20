package dev.dini.account.service.account;

import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.AccountResponseDTO;
import dev.dini.account.service.dto.CreateAccountRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    List<Account> getAccountsByCustomerId(Integer customerId);

    Account createAccount(CreateAccountRequestDTO createAccountRequestDTO);

    // Retrieve account details
    Account getAccount(Integer accountId);

    // Close account
    void closeAccount(Integer accountId);

    // Get account balance
    BigDecimal getBalance(Integer accountId);

    AccountResponseDTO updateAccount(Integer accountId, AccountRequestDTO accountRequestDTO);
    void transferFunds(Integer fromAccountId, Integer toAccountId, BigDecimal amount);

    Account changeAccountType(Integer accountId, AccountType newAccountType);

    void setOverdraftProtection(Integer accountId, boolean enabled);

    void freezeAccount(Integer accountId);
    void unfreezeAccount(Integer accountId);


    void addAccountHolder(Integer accountId, Integer customerId);
    void removeAccountHolder(Integer accountId, Integer customerId);

    AccountStatus checkAccountStatus(Integer accountId);

    void setTransactionLimit(Integer accountId, BigDecimal limit);


}
