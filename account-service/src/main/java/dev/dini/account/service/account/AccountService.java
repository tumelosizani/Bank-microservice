package dev.dini.account.service.account;

import dev.dini.account.service.dto.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountService {
    void processTransaction(UUID fromAccountId, UUID toAccountId, BigDecimal amount);

    void linkAccountToCustomer(UUID accountId, UUID customerId);

    List<Account> getAccountsByCustomerId(UUID customerId);

    Account createAccount(CreateAccountRequestDTO createAccountRequestDTO);

    // Retrieve account details
    Account getAccount(UUID accountId);

    // Close account
    void closeAccount(UUID accountId);

    // Get account balance
    BigDecimal getBalance(UUID accountId);

    AccountResponseDTO updateAccount(UUID accountId, AccountRequestDTO accountRequestDTO);
    void transferFunds(UUID fromAccountId, UUID toAccountId, BigDecimal amount);

    Account changeAccountType(UUID accountId, AccountType newAccountType);

    void setOverdraftProtection(UUID accountId, boolean enabled);

    void freezeAccount(UUID accountId);
    void unfreezeAccount(UUID accountId);


    void addAccountHolder(UUID accountId, UUID customerId);
    void removeAccountHolder(UUID accountId, UUID customerId);

    AccountStatus checkAccountStatus(UUID accountId);


    void setTransactionLimit(UUID accountId, BigDecimal limit);

    // Calculate interest
    InterestCalculationResponseDTO calculateInterest(InterestCalculationRequestDTO request);
}
