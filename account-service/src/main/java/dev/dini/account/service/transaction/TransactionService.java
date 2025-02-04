package dev.dini.account.service.transaction;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.audit.AccountAuditService;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.security.AccountSecurityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final AccountRepository accountRepository;
    private final TransactionFeignClient transactionFeignClient;
    private final AccountAuditService accountAuditService;
    private final AccountSecurityService accountSecurityService;

    @Transactional
    public void transferFunds(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        logger.info("Processing transaction from account ID: {} to account ID: {} with amount: {}", fromAccountId, toAccountId, amount);

        if (fromAccountId == null || toAccountId == null || amount == null) {
            logger.error("Transaction data cannot be null");
            throw new IllegalArgumentException("Transaction data cannot be null");
        }

        // Retrieve accounts to validate balances and existence
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));

        // Validate funds in the 'from' account
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Save updated accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Create a transaction DTO
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setFromAccountId(fromAccountId);
        transactionDTO.setToAccountId(toAccountId);
        transactionDTO.setAmount(amount);

        // Call the external Transaction Service using Feign Client to log the transaction
        transactionFeignClient.createTransaction(transactionDTO);

        // Log the transaction event in the audit log
        accountAuditService.logAccountEvent(fromAccountId, "PROCESS_TRANSACTION", "Transaction processed successfully");

        logger.info("Transaction processed successfully from account ID: {} to account ID: {} with amount: {}", fromAccountId, toAccountId, amount);
    }

    public void setTransactionLimit(UUID accountId, BigDecimal limit) {
        accountSecurityService.setTransactionLimit(accountId, limit);
    }

}
