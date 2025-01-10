package dev.dini.transaction.service.transaction;

import dev.dini.transaction.service.dto.TransactionRequestDTO;
import dev.dini.transaction.service.dto.TransactionResponseDTO;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    // Create a new transaction
    TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO);

    // Get all transactions for an account
    List<TransactionResponseDTO> getTransactionsForAccount(Integer accountId);

    // Complete a transaction
    TransactionResponseDTO completeTransaction(Integer transactionId);

    // Cancel a transaction
    TransactionResponseDTO cancelTransaction(Integer transactionId);

    // Update transaction status
    TransactionResponseDTO updateTransactionStatus(Integer transactionId, TransactionStatus status);

    // Get the details of a specific transaction
    Optional<TransactionResponseDTO> getTransactionDetails(Integer transactionId);

    // Check if a transaction is fraudulent
    boolean checkForFraud(Transaction transaction);

    // Audit a transaction
    void auditTransaction(Integer transactionId, Integer userId);

    void deleteTransaction(Integer transactionId);

}
