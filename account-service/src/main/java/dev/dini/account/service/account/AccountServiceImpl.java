package dev.dini.account.service.account;

import dev.dini.account.service.audit.AccountAuditService;
import dev.dini.account.service.dto.*;
import dev.dini.account.service.transaction.TransactionService;
import dev.dini.account.service.interest.InterestCalculationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountManagementService accountManagementService;
    private final AccountInfoService accountInfoService;
    private final TransactionService transactionService;
    private final InterestCalculationService interestCalculationService;

    @Override
    public Account createAccount(CreateAccountRequestDTO createAccountRequestDTO) {
        return accountManagementService.createAccount(createAccountRequestDTO);
    }

    @Override
    public Account getAccount(UUID accountId) {
        return accountInfoService.getAccount(accountId);
    }

    @Override
    public void closeAccount(UUID accountId) {
        accountManagementService.closeAccount(accountId);
    }

    @Override
    public BigDecimal getBalance(UUID accountId) {
        return accountInfoService.getBalance(accountId);
    }

    @Override
    public AccountResponseDTO updateAccount(UUID accountId, AccountRequestDTO accountRequestDTO) {
        return accountManagementService.updateAccount(accountId, accountRequestDTO);
    }

    @Override
    @Transactional
    public void transferFunds(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        transactionService.transferFunds(fromAccountId, toAccountId, amount);
        logger.info("Transaction from account {} to account {} of amount {} processed successfully", fromAccountId, toAccountId, amount);
    }

    @Override
    public Account changeAccountType(UUID accountId, AccountType newAccountType) {
        return accountManagementService.changeAccountType(accountId, newAccountType);
    }

    @Override
    public void setOverdraftProtection(UUID accountId, boolean enabled) {
        accountManagementService.setOverdraftProtection(accountId, enabled);
    }

    @Override
    public void freezeAccount(UUID accountId) {
        accountManagementService.freezeAccount(accountId);
    }

    @Override
    public void unfreezeAccount(UUID accountId) {
        accountManagementService.unfreezeAccount(accountId);
    }

    @Override
    public void addAccountHolder(UUID accountId, UUID customerId) {
        accountManagementService.addAccountHolder(accountId, customerId);
    }

    @Override
    public void removeAccountHolder(UUID accountId, UUID customerId) {
        accountManagementService.removeAccountHolder(accountId, customerId);
    }

    @Override
    public AccountStatus checkAccountStatus(UUID accountId) {
        return accountInfoService.checkAccountStatus(accountId);
    }

    @Override
    public void setTransactionLimit(UUID accountId, BigDecimal limit) {
        accountManagementService.setTransactionLimit(accountId, limit);
    }

    @Override
    public void processTransaction(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        transactionService.transferFunds(fromAccountId, toAccountId, amount);
    }

    @Override
    public void linkAccountToCustomer(UUID accountId, UUID customerId) {
        accountManagementService.linkAccountToCustomer(accountId, customerId);
    }

    @Override
    public List<Account> getAccountsByCustomerId(UUID customerId) {
        return accountInfoService.getAccountsByCustomerId(customerId);
    }

    @Override
    public InterestCalculationResponseDTO calculateInterest(InterestCalculationRequestDTO request) {
        UUID accountId = request.getAccountId();
        logger.info("Calculating interest for account ID: {}", accountId);
        return interestCalculationService.calculateInterest(request);
    }
}