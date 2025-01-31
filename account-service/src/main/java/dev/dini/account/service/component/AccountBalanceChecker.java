package dev.dini.account.service.component;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
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
        return accountOptional.map(account -> account.getBalance().compareTo(amount) >= 0).orElse(false);
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
}
