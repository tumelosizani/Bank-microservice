package dev.dini.account.service.component;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountBalanceChecker {

    private static final Logger logger = LoggerFactory.getLogger(AccountBalanceChecker.class);

    private final AccountRepository accountRepository;

    public boolean hasSufficientBalance(UUID accountId, BigDecimal amount) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            BigDecimal balance = accountOptional.get().getBalance();
            if (balance.compareTo(amount) < 0) {
                logger.warn("Account ID {} has insufficient balance. Current balance: {}, required: {}", accountId, balance, amount);
                return false;
            }
            return true;
        } else {
            logger.error("Account with ID {} not found", accountId);
            throw new AccountNotFoundException(accountId);
        }
    }

    public BigDecimal getAccountBalance(UUID accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            BigDecimal balance = accountOptional.get().getBalance();
            logger.info("Account balance for ID {}: {}", accountId, balance);
            return balance;
        } else {
            logger.warn("Account with ID {} not found", accountId);
            return BigDecimal.ZERO;
        }
    }

    public void updateAccountBalance(UUID accountId, BigDecimal newBalance) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setBalance(newBalance);
            accountRepository.save(account);
            logger.info("Account balance for ID {} updated to {}", accountId, newBalance);
        } else {
            logger.warn("Account with ID {} not found", accountId);
        }
    }

}
