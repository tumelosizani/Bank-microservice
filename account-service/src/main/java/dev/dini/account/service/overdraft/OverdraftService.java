package dev.dini.account.service.overdraft;


import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.exception.InsufficientFundsException;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class OverdraftService {

    private final AccountRepository accountRepository;
    private final OverdraftProtectionValidator overdraftProtectionValidator;

    public OverdraftService(AccountRepository accountRepository, OverdraftProtectionValidator overdraftProtectionValidator) {
        this.accountRepository = accountRepository;
        this.overdraftProtectionValidator = overdraftProtectionValidator;
    }

    /**
     * Check if the account has sufficient funds to process a withdrawal.
     *
     * @param accountId The ID of the account to check
     * @param amount    The amount to withdraw
     * @throws InsufficientFundsException If insufficient funds and overdraft is not available.
     */
    public void validateWithdrawal(UUID accountId, BigDecimal amount) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found.");
        }

        Account account = accountOpt.get();
        BigDecimal accountBalance = account.getBalance();

        // Ensure that the account type supports overdraft protection
        if (!overdraftProtectionValidator.isOverdraftProtectionAllowed(account)) {
            throw new IllegalArgumentException("Overdraft protection is not allowed for this account type.");
        }

        // Check if the account has enough balance for the withdrawal
        if (accountBalance.compareTo(amount) < 0) {
            if (account.getOverdraftLimit().compareTo(BigDecimal.ZERO) <= 0) {
                // If no overdraft protection is available, throw exception
                throw new InsufficientFundsException("Insufficient funds and overdraft protection is not enabled.");
            }

            // Calculate the available overdraft amount
            BigDecimal totalAvailableFunds = accountBalance.add(account.getOverdraftLimit());

            if (totalAvailableFunds.compareTo(amount) < 0) {
                throw new InsufficientFundsException("Insufficient funds including overdraft protection.");
            }

            // Deduct from the overdraft limit if necessary
            BigDecimal overdraftUsed = amount.subtract(accountBalance);
            account.setOverdraftLimit(account.getOverdraftLimit().subtract(overdraftUsed));
            accountRepository.save(account); // Update the account with the new overdraft limit
        }
    }

    /**
     * Enable overdraft protection on the account.
     *
     * @param accountId       The ID of the account to enable overdraft on
     * @param overdraftAmount The amount of overdraft protection to enable
     */
    public void enableOverdraft(UUID accountId, BigDecimal overdraftAmount) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found.");
        }

        Account account = accountOpt.get();

        // Ensure that the account type supports overdraft protection
        if (!overdraftProtectionValidator.isOverdraftProtectionAllowed(account)) {
            throw new IllegalArgumentException("Overdraft protection is not allowed for this account type.");
        }

        account.setOverdraftLimit(overdraftAmount);
        accountRepository.save(account);
    }

    /**
     * Disable overdraft protection on the account.
     *
     * @param accountId The ID of the account to disable overdraft on
     */
    public void disableOverdraft(UUID accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found.");
        }

        Account account = accountOpt.get();

        // Ensure that the account type supports overdraft protection
        if (!overdraftProtectionValidator.isOverdraftProtectionAllowed(account)) {
            throw new IllegalArgumentException("Overdraft protection is not allowed for this account type.");
        }

        account.setOverdraftLimit(BigDecimal.ZERO);
        accountRepository.save(account);
    }
}
