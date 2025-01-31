package dev.dini.account.service.account;

import dev.dini.account.service.component.*;
import dev.dini.account.service.customer.CustomerDTO;
import dev.dini.account.service.customer.CustomerServiceClient;
import dev.dini.account.service.dto.*;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.exception.InsufficientFundsException;
import dev.dini.account.service.mapper.AccountMapper;
import dev.dini.account.service.transaction.TransactionDTO;
import dev.dini.account.service.transaction.TransactionFeignClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionFeignClient transactionFeignClient;
    private final CustomerServiceClient customerServiceClient;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountAuditLog accountAuditLog;
    private final AccountNotification accountNotification;
    private final AccountVerification accountVerification;
    private final InterestCalculation interestCalculation;
    private final AccountLocking accountLocking;
    private final AccountBalanceChecker accountBalanceChecker;

    @Override
    public Account createAccount(CreateAccountRequestDTO createAccountRequestDTO) {
        logger.info("Creating a new account for customer ID: {}", createAccountRequestDTO.getCustomerId());

        CustomerDTO customerDTO = customerServiceClient.getCustomerById(createAccountRequestDTO.getCustomerId());

        String generatedAccountNumber = accountNumberGenerator.generateUniqueAccountNumber();

        Account newAccount = accountMapper.toAccountFromCreateRequest(createAccountRequestDTO);
        newAccount.setCustomerId(customerDTO.getCustomerId());
        newAccount.setBalance(BigDecimal.ZERO); // Initial balance is zero
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setUpdatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(newAccount);

        accountAuditLog.logAccountEvent(savedAccount.getAccountId(), "CREATE_ACCOUNT", "New account created");
        accountNotification.sendAccountCreationNotification(savedAccount.getCustomerId(), savedAccount.getAccountId());

        logger.info("Account created successfully with ID: {}", savedAccount.getAccountId());
        return savedAccount;
    }

    @Override
    public Account getAccount(UUID accountId) {
        logger.info("Fetching account with ID: {}", accountId);
        accountVerification.verifyAccountExists(accountId);
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public void closeAccount(UUID accountId) {
        logger.info("Closing account with ID: {}", accountId);

        // Check if the account exists and close it
        accountLocking.closeAccount(accountId);

        logger.info("Account closed successfully with ID: {}", accountId);

        // Log the account closure event
        accountAuditLog.logAccountEvent(accountId, "CLOSE_ACCOUNT", "Account closed");

        // Send a notification about the account closure
        Account account = accountLocking.getAccount(accountId);
        accountNotification.sendAccountClosureNotification(account.getCustomerId(), accountId);
    }

    @Override
    public BigDecimal getBalance(UUID accountId) {
        logger.info("Fetching balance for account ID: {}", accountId);
        BigDecimal balance = accountBalanceChecker.getAccountBalance(accountId);
        return balance;
    }

    @Override
    public AccountResponseDTO updateAccount(UUID accountId, AccountRequestDTO accountRequestDTO) {
        logger.info("Updating account with ID: {}", accountId);
        Account existingAccount = getAccount(accountId);
        accountMapper.updateAccountFromDto(accountRequestDTO, existingAccount);
        existingAccount.setUpdatedAt(LocalDateTime.now());
        Account updatedAccount = accountRepository.save(existingAccount);

        accountAuditLog.logAccountEvent(accountId, "UPDATE_ACCOUNT", "Account updated");

        logger.info("Account updated successfully with ID: {}", accountId);
        return accountMapper.toAccountResponseDTOAfterUpdate(updatedAccount);
    }

    @Override
    public void transferFunds(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        logger.info("Transferring funds from account ID: {} to account ID: {}", fromAccountId, toAccountId);

        // Check if the account is locked
        boolean isAccountLocked = accountLocking.isAccountLocked(fromAccountId);
        if (isAccountLocked) {
            logger.error("Account ID: {} is locked", fromAccountId);
            throw new IllegalStateException("Account is locked due to too many failed attempts");
        }


        // Check if sufficient balance exists in the account
        boolean hasSufficientBalance = accountBalanceChecker.hasSufficientBalance(fromAccountId, amount);
        if (!hasSufficientBalance) {
            logger.error("Insufficient funds in account ID: {}", fromAccountId);
            throw new InsufficientFundsException("Insufficient funds in account: " + fromAccountId);
        }


        // Proceed with the transaction
        Account fromAccount = getAccount(fromAccountId);
        Account toAccount = getAccount(toAccountId);

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        processTransaction(fromAccountId, toAccountId, amount);

        accountAuditLog.logAccountEvent(fromAccountId, "TRANSFER_FUNDS",
                "Transferred " + amount + " to account " + toAccountId);
        logger.info("Funds transferred successfully from account ID: {} to account ID: {}", fromAccountId, toAccountId);
    }

    @Override
    public Account changeAccountType(UUID accountId, AccountType newAccountType) {
        logger.info("Changing account type for account ID: {} to {}", accountId, newAccountType);
        Account account = getAccount(accountId);
        account.setAccountType(newAccountType);
        account.setUpdatedAt(LocalDateTime.now());
        Account updatedAccount = accountRepository.save(account);
        logger.info("Account type changed successfully for account ID: {}", accountId);
        return updatedAccount;
    }

    @Override
    public void setOverdraftProtection(UUID accountId, boolean enabled) {
        logger.info("Setting overdraft protection for account ID: {} to {}", accountId, enabled);
        Account account = getAccount(accountId);
        account.setOverdraftProtection(enabled);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        logger.info("Overdraft protection set successfully for account ID: {}", accountId);
    }

    @Override
    public void freezeAccount(UUID accountId) {
        logger.info("Freezing account with ID: {}", accountId);
        accountLocking.freezeAccount(accountId);
        logger.info("Account frozen successfully with ID: {}", accountId);

        accountAuditLog.logAccountEvent(accountId, "FREEZE_ACCOUNT", "Account frozen");
    }

    @Override
    public void unfreezeAccount(UUID accountId) {
        logger.info("Unfreezing account with ID: {}", accountId);
        accountLocking.unfreezeAccount(accountId);
        logger.info("Account unfrozen successfully with ID: {}", accountId);

        accountAuditLog.logAccountEvent(accountId, "UNFREEZE_ACCOUNT", "Account unfrozen");
    }

    @Override
    public void addAccountHolder(UUID accountId, UUID customerId) {
        logger.info("Adding account holder with customer ID: {} to account ID: {}", customerId, accountId);
        Account account = getAccount(accountId);
        account.addAccountHolder(customerId);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        logger.info("Account holder added successfully with customer ID: {} to account ID: {}", customerId, accountId);
    }

    @Override
    public void removeAccountHolder(UUID accountId, UUID customerId) {
        logger.info("Removing account holder with customer ID: {} from account ID: {}", customerId, accountId);
        Account account = getAccount(accountId);
        account.removeAccountHolder(customerId);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        logger.info("Account holder removed successfully with customer ID: {} from account ID: {}", customerId, accountId);
    }

    @Override
    public AccountStatus checkAccountStatus(UUID accountId) {
        logger.info("Checking status for account ID: {}", accountId);
        Account account = getAccount(accountId);
        return account.getStatus();
    }

    @Override
    public void setTransactionLimit(UUID accountId, BigDecimal limit) {
        logger.info("Setting transaction limit for account ID: {} to {}", accountId, limit);
        Account account = getAccount(accountId);
        account.setTransactionLimit(limit);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        logger.info("Transaction limit set successfully for account ID: {}", accountId);
    }

    @Override
    public void processTransaction(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        logger.info("Processing transaction from account ID: {} to account ID: {} with amount: {}", fromAccountId, toAccountId, amount);

        if (fromAccountId == null || toAccountId == null || amount == null) {
            logger.error("Transaction data cannot be null");
            throw new IllegalArgumentException("Transaction data cannot be null");
        }

        // Create a transaction DTO
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setFromAccountId(fromAccountId);
        transactionDTO.setToAccountId(toAccountId);
        transactionDTO.setAmount(amount);

        // Call the Transaction service using Feign Client
        transactionFeignClient.createTransaction(transactionDTO);

        accountAuditLog.logAccountEvent(fromAccountId, "PROCESS_TRANSACTION", "Transaction processed");
        logger.info("Transaction processed successfully from account ID: {} to account ID: {} with amount: {}", fromAccountId, toAccountId, amount);
    }

    @Override
    public void linkAccountToCustomer(UUID accountId, UUID customerId) {
        logger.info("Linking account ID: {} to customer ID: {}", accountId, customerId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No account found with ID: " + accountId));
        account.setCustomerId(customerId);
        accountRepository.save(account);
        logger.info("Account ID: {} linked successfully to customer ID: {}", accountId, customerId);
    }

    @Override
    public List<Account> getAccountsByCustomerId(UUID customerId) {
        logger.info("Fetching accounts for customer ID: {}", customerId);
        CustomerDTO customerDTO = customerServiceClient.getCustomerById(customerId);

        if (customerDTO == null){
            logger.error("No customer found with ID: {}", customerId);
            throw new AccountNotFoundException("No customer found with ID: " + customerId);
        }
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        logger.info("Found {} accounts for customer ID: {}", accounts.size(), customerId);
        return accounts;
    }

    @Override
    public InterestCalculationResponseDTO calculateInterest(InterestCalculationRequestDTO request) {
        UUID accountId = request.getAccountId();
        logger.info("Calculating interest for account ID: {}", accountId);
        Account account = getAccount(accountId);
        BigDecimal interestAmount = interestCalculation.calculateInterest(account);
        account.setBalance(account.getBalance().add(interestAmount));
        accountRepository.save(account);

        accountAuditLog.logAccountEvent(accountId, "INTEREST_CALCULATION", "Interest calculated");

        InterestCalculationResponseDTO response = new InterestCalculationResponseDTO();
        response.setInterestAmount(interestAmount);
        return response;
    }
}