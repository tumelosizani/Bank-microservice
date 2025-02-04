package dev.dini.account.service.account;

import dev.dini.account.service.audit.AccountAuditService;
import dev.dini.account.service.component.AccountNumberGenerator;
import dev.dini.account.service.customer.CustomerDTO;
import dev.dini.account.service.customer.CustomerServiceClient;
import dev.dini.account.service.dto.*;
import dev.dini.account.service.interest.InterestCalculationService;
import dev.dini.account.service.mapper.AccountMapper;
import dev.dini.account.service.notification.AccountNotificationService;
import dev.dini.account.service.security.AccountSecurityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountManagementService {

    private static final Logger logger = LoggerFactory.getLogger(AccountManagementService.class);

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountAuditService accountAuditLog;
    private final AccountNotificationService accountNotification;
    private final AccountNumberGenerator accountNumberGenerator;
    private final CustomerServiceClient customerServiceClient;
    private final AccountInfoService accountInfoService;
    private final AccountSecurityService accountSecurityService;
    private final InterestCalculationService interestCalculationService;

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

    public AccountResponseDTO updateAccount(UUID accountId, AccountRequestDTO accountRequestDTO) {
        logger.info("Updating account with ID: {}", accountId);
        Account existingAccount = accountInfoService.getAccount(accountId);
        accountMapper.updateAccountFromDto(accountRequestDTO, existingAccount);
        existingAccount.setUpdatedAt(LocalDateTime.now());
        Account updatedAccount = accountRepository.save(existingAccount);

        accountAuditLog.logAccountEvent(accountId, "UPDATE_ACCOUNT", "Account updated");

        logger.info("Account updated successfully with ID: {}", accountId);
        return accountMapper.toAccountResponseDTOAfterUpdate(updatedAccount);
    }

    public void closeAccount(UUID accountId) {
        logger.info("Closing account with ID: {}", accountId);
        accountSecurityService.closeAccount(accountId);
    }

    public Account changeAccountType(UUID accountId, AccountType newAccountType) {
        logger.info("Changing account type for account ID: {} to {}", accountId, newAccountType);
        Account account = accountInfoService.getAccount(accountId);

        // Validate if the account type change is allowed
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
        return (currentType == AccountType.CHECKING && newType == AccountType.SAVINGS) ||
                (currentType == AccountType.SAVINGS && newType == AccountType.CHECKING);
    }

    public void addAccountHolder(UUID accountId, UUID customerId) {
        logger.info("Adding account holder with customer ID: {} to account ID: {}", customerId, accountId);
        Account account = accountInfoService.getAccount(accountId);
        account.addAccountHolder(customerId);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        logger.info("Account holder added successfully with customer ID: {} to account ID: {}", customerId, accountId);
    }

    public void removeAccountHolder(UUID accountId, UUID customerId) {
        logger.info("Removing account holder with customer ID: {} from account ID: {}", customerId, accountId);
        Account account = accountInfoService.getAccount(accountId);
        account.removeAccountHolder(customerId);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        logger.info("Account holder removed successfully with customer ID: {} from account ID: {}", customerId, accountId);
    }

    public void linkAccountToCustomer(UUID accountId, UUID customerId) {
        logger.info("Linking account ID: {} to customer ID: {}", accountId, customerId);
        Account account = accountInfoService.getAccount(accountId);
        account.setCustomerId(customerId);
        accountRepository.save(account);
        logger.info("Account ID: {} linked successfully to customer ID: {}", accountId, customerId);
    }

    public void setTransactionLimit(UUID accountId, BigDecimal limit) {
        logger.info("Setting transaction limit for account ID: {} to {}", accountId, limit);
        Account account = accountInfoService.getAccount(accountId);
        account.setTransactionLimit(limit);
        accountRepository.save(account);
        accountAuditLog.logAccountEvent(accountId, "SET_TRANSACTION_LIMIT", "Transaction limit set to " + limit);
        accountNotification.sendTransactionLimitNotification(account.getCustomerId(), accountId, limit);
        logger.info("Transaction limit set successfully for account ID: {}", accountId);
    }

    public void setOverdraftProtection(UUID accountId, boolean enabled) {
        logger.info("Setting overdraft protection for account ID: {} to {}", accountId, enabled);
        Account account = accountInfoService.getAccount(accountId);
        account.setOverdraftProtection(enabled);
        accountRepository.save(account);
        accountAuditLog.logAccountEvent(accountId, "SET_OVERDRAFT_PROTECTION", "Overdraft protection " + (enabled ? "enabled" : "disabled"));
        accountNotification.sendOverdraftProtectionNotification(account.getCustomerId(), accountId, enabled);
        logger.info("Overdraft protection set successfully for account ID: {}", accountId);
    }

    public void freezeAccount(UUID accountId) {
        logger.info("Freezing account with ID: {}", accountId);
        accountSecurityService.freezeAccount(accountId);
    }

    public void unfreezeAccount(UUID accountId) {
        logger.info("Unfreezing account with ID: {}", accountId);
        accountSecurityService.unfreezeAccount(accountId);
    }

    // New methods for interest calculation
    public InterestCalculationResponseDTO calculateInterest(UUID accountId) {
        logger.info("Calculating interest for account ID: {}", accountId);
        InterestCalculationRequestDTO request = new InterestCalculationRequestDTO(accountId);
        return interestCalculationService.calculateInterest(request);
    }

    public void applyInterest() {
        logger.info("Applying interest to all eligible accounts");
        interestCalculationService.applyInterest();
    }
}