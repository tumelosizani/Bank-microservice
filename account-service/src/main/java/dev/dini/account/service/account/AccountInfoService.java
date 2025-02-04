package dev.dini.account.service.account;

import dev.dini.account.service.component.AccountBalanceChecker;
import dev.dini.account.service.customer.CustomerDTO;
import dev.dini.account.service.customer.CustomerServiceClient;
import dev.dini.account.service.dto.AccountResponseDTO;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountInfoService {

    private static final Logger logger = LoggerFactory.getLogger(AccountInfoService.class);

    private final AccountRepository accountRepository;
    private final CustomerServiceClient customerServiceClient;
    private final AccountMapper accountMapper;
    private final AccountBalanceChecker accountBalanceChecker;

    public Account getAccount(UUID accountId) {
        logger.info("Fetching account with ID: {}", accountId);
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    public BigDecimal getBalance(UUID accountId) {
        logger.info("Fetching balance for account ID: {}", accountId);
        return accountBalanceChecker.getAccountBalance(accountId);
    }

    public AccountStatus checkAccountStatus(UUID accountId) {
        logger.info("Checking status for account ID: {}", accountId);
        Account account = getAccount(accountId);
        return account.getStatus();
    }

    public List<Account> getAccountsByCustomerId(UUID customerId) {
        logger.info("Fetching accounts for customer ID: {}", customerId);
        CustomerDTO customerDTO = customerServiceClient.getCustomerById(customerId);

        if (customerDTO == null) {
            logger.error("No customer found with ID: {}", customerId);
            throw new AccountNotFoundException(customerId);
        }
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        logger.info("Found {} accounts for customer ID: {}", accounts.size(), customerId);
        return accounts;
    }

    public AccountResponseDTO getAccountResponse(UUID accountId) {
        Account account = getAccount(accountId);
        return accountMapper.toAccountResponseDTO(account);
    }
}