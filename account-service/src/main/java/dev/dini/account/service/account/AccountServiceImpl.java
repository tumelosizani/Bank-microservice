package dev.dini.account.service.account;

import dev.dini.account.service.audit.AccountAuditService;
import dev.dini.account.service.component.*;
import dev.dini.account.service.customer.CustomerDTO;
import dev.dini.account.service.customer.CustomerServiceClient;
import dev.dini.account.service.dto.*;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.interest.InterestCalculationService;
import dev.dini.account.service.mapper.AccountMapper;
import dev.dini.account.service.notification.AccountNotificationService;
import dev.dini.account.service.overdraft.OverdraftService;
import dev.dini.account.service.security.AccountSecurityService;
import dev.dini.account.service.transaction.TransactionDTO;
import dev.dini.account.service.transaction.TransactionFeignClient;
import dev.dini.account.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TransactionService transactionService;
    private final AccountSecurityService securityService;

    private final TransactionFeignClient transactionFeignClient;
    private final CustomerServiceClient customerServiceClient;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountAuditService accountAuditLog;
    private final AccountNotificationService accountNotification;
    private final AccountVerification accountVerification;
    private final InterestCalculationService interestCalculation;
    private final AccountBalanceChecker accountBalanceChecker;
    private final OverdraftService overdraftService;
    private final AccountSecurityService accountSecurityService;

    @Override
    public Account createAccount(CreateAccountRequestDTO createAccountRequestDTO) {
        logger.info("Creating a new account for customer ID: {}", createAccountRequestDTO.getCustomerId());

        // Validate customer existence
        CustomerDTO customerDTO = customerServiceClient.getCustomerById(createAccountRequestDTO.getCustomerId());

        // Generate unique account number
        AccountType accountType = createAccountRequestDTO.getAccountType();
        String generatedAccountNumber = accountNumberGenerator.generateUniqueAccountNumber(accountType);

        // Create account from request DTO
        Account newAccount = accountMapper.toAccountFromCreateRequest(createAccountRequestDTO);
        newAccount.setCustomerId(customerDTO.getCustomerId());
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setUpdatedAt(LocalDateTime.now());

        // Save the new account to the repository
        Account savedAccount = accountRepository.save(newAccount);

        // Log the account creation event and send notification
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
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public void closeAccount(UUID accountId) {
        accountSecurityService.closeAccount(accountId);
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
    @Transactional
    public void transferFunds(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        transactionService.transferFunds(fromAccountId, toAccountId, amount);
        logger.info("Transaction from account {} to account {} of amount {} processed successfully", fromAccountId, toAccountId, amount);
    }

    @Override
    public Account changeAccountType(UUID accountId, AccountType newAccountType) {
        logger.info("Changing account type for account ID: {} to {}", accountId, newAccountType);
        Account account = getAccount(accountId);

        // validate if the account type change is allowed
        if (!isAccountTypeChangePermissible(account.getAccountType(), newAccountType)) {
            logger.error("Account type change not allowed from {} to {}", account.getAccountType(), newAccountType);
            throw new IllegalArgumentException("Account type change not allowed");
        }

        account.setAccountType(newAccountType);
        account.setUpdatedAt(LocalDateTime.now());
        Account updatedAccount = accountRepository.save(account);
        logger.info("Account type changed successfully for account ID: {}", accountId);
        return updatedAccount;
    }

    private boolean isAccountTypeChangePermissible(AccountType currentType, AccountType newType) {
        // Define the permissible account type changes
        if (currentType == AccountType.CHECKING && newType == AccountType.SAVINGS) {
            return true;
        } else if (currentType == AccountType.SAVINGS && newType == AccountType.CHECKING) {
            return true;
        }
        return false;
    }

    @Override
    public void setOverdraftProtection(UUID accountId, boolean enabled) {
        logger.info("Setting overdraft protection for account ID: {} to {}", accountId, enabled);

        // Call the overdraft service to enable/disable protection
        if (enabled) {
            overdraftService.enableOverdraft(accountId, BigDecimal.valueOf(1000));  // Example overdraft amount
        } else {
            overdraftService.disableOverdraft(accountId);
        }

        logger.info("Overdraft protection set successfully for account ID: {}", accountId);
    }


    @Override
    public void freezeAccount(UUID accountId) {
        securityService.freezeAccount(accountId);
    }

    @Override
    public void unfreezeAccount(UUID accountId) {
        securityService.unfreezeAccount(accountId);
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

    public void withdraw(UUID accountId, BigDecimal amount) {
        // Validate if the withdrawal is possible, considering overdraft limits
        overdraftService.validateWithdrawal(accountId, amount);

        // Proceed with the actual withdrawal logic (subtracting from account balance)
        // For simplicity, we're just updating the balance directly.
        Account account = accountRepository.findById(accountId).orElseThrow();
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Override
    public void setTransactionLimit(UUID accountId, BigDecimal limit) {
       accountSecurityService.setTransactionLimit(accountId, limit);
    }

    @Override
    public void processTransaction(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        transactionService.transferFunds(fromAccountId, toAccountId, amount);
    }

    @Override
    public void linkAccountToCustomer(UUID accountId, UUID customerId) {
        logger.info("Linking account ID: {} to customer ID: {}", accountId, customerId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setCustomerId(customerId);
        accountRepository.save(account);
        logger.info("Account ID: {} linked successfully to customer ID: {}", accountId, customerId);
    }

    @Override
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