package dev.dini.Transaction_Service.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public Transaction createTransaction(Integer accountId, BigDecimal amount, TransactionType transactionType,
                                         String description) {
        // Basic validation (e.g., amount must be positive)
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        // Create a new transaction object
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setStatus(TransactionStatus.PENDING); // Initial status is "PENDING"
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setDescription(description);

        // Save the transaction in the database
        logger.info("Creating transaction for account ID: {} with amount: {}", accountId, amount);
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public Optional<Transaction> getTransaction(Integer transactionId) {
        return transactionRepository.findById(transactionId);
    }

    @Transactional
    @Override
    public Transaction completeTransaction(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        // Update the status to "COMPLETED"
        transaction.setStatus(TransactionStatus.COMPLETED);

        // Log the status change
        logger.info("Completing transaction with ID: {}", transactionId);

        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public Transaction cancelTransaction(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        // Update the status to "CANCELLED"
        transaction.setStatus(TransactionStatus.CANCELLED);

        // Log the status change
        logger.info("Cancelling transaction with ID: {}", transactionId);

        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void deleteTransaction(Integer transactionId) {
        // Ensure the transaction exists before attempting to delete it
        if (!transactionRepository.existsById(transactionId)) {
            throw new TransactionNotFoundException("Transaction not found");
        }

        logger.info("Deleting transaction with ID: {}", transactionId);
        transactionRepository.deleteById(transactionId);
    }
}
