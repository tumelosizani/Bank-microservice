package dev.dini.account.service.account;

import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.CreateAccountRequestDTO;

import java.math.BigDecimal;

public interface AccountService {
    Account createAccount(CreateAccountRequestDTO createAccountRequestDTO);

    // Retrieve account details
    Account getAccount(Integer accountId);

    // Close account
    void closeAccount(Integer accountId);

    // Get account balance
    BigDecimal getBalance(Integer accountId);

    Account updateAccount(Integer accountId, AccountRequestDTO accountRequestDTO);
}
