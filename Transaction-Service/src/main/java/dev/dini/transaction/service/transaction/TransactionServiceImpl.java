package dev.dini.transaction.service.transaction;

import dev.dini.transaction.service.account.AccountDTO;
import dev.dini.transaction.service.account.AccountNotFoundException;
import dev.dini.transaction.service.account.AccountServiceClient;
import dev.dini.transaction.service.dto.TransactionRequestDTO;
import dev.dini.transaction.service.dto.TransactionResponseDTO;
import dev.dini.transaction.service.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountServiceClient accountServiceClient;

    @Override
    public List<TransactionResponseDTO> getTransactionsForAccount(Integer accountId) {
        // Find transactions where the account is either the sender or receiver
        List<Transaction> transactions = transactionRepository.findBySenderAccountIdOrReceiverAccountId(accountId, accountId);

        // Map the transactions to response DTOs
        return transactions.stream()
                .map(transactionMapper::toTransactionResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {
        // Call AccountService to check if sender and receiver accounts exist
        AccountDTO senderAccount = accountServiceClient.getAccountById(transactionRequestDTO.getSenderAccountId());
        AccountDTO receiverAccount = accountServiceClient.getAccountById(transactionRequestDTO.getReceiverAccountId());

        if (senderAccount == null || receiverAccount == null) {
            // Handle account not found
            throw new AccountNotFoundException("Sender or receiver account not found.");
        }

        // Create transaction entity
        Transaction transaction = new Transaction();
        transaction.setSenderAccountId(transactionRequestDTO.getSenderAccountId());
        transaction.setReceiverAccountId(transactionRequestDTO.getReceiverAccountId());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setTransactionType(transactionRequestDTO.getTransactionType());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDescription(transactionRequestDTO.getDescription());

        // Save transaction in repository
        transaction = transactionRepository.save(transaction);

        // Map the transaction entity to response DTO
        return transactionMapper.toTransactionResponseDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO completeTransaction(Integer transactionId) {
        // Fetch the transaction from the repository
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Update the status to completed
        transaction.setStatus(TransactionStatus.COMPLETED);

        // Save the updated transaction
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Optionally update accounts, notify parties, etc.

        // Map to DTO and return
        return transactionMapper.toTransactionResponseDTO(updatedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO cancelTransaction(Integer transactionId) {
        // Fetch the transaction from the repository
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Update the status to cancelled
        transaction.setStatus(TransactionStatus.CANCELLED);

        // Revert account balances if necessary
        if (transaction.getTransactionType() == TransactionType.TRANSFER) {
            // Reverse the amount in both sender and receiver accounts
            accountServiceClient.addFunds(transaction.getSenderAccountId(), transaction.getAmount());
            accountServiceClient.deductFunds(transaction.getReceiverAccountId(), transaction.getAmount());
        }

        // Save the updated transaction
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Map to DTO and return
        return transactionMapper.toTransactionResponseDTO(updatedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO updateTransactionStatus(Integer transactionId, TransactionStatus status) {
        // Fetch the transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Validate status change
        if (transaction.getStatus() == TransactionStatus.COMPLETED || transaction.getStatus() == TransactionStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot update a completed or cancelled transaction.");
        }

        // Update the status
        transaction.setStatus(status);

        // Save the updated transaction
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Map to DTO and return
        return transactionMapper.toTransactionResponseDTO(updatedTransaction);
    }

    @Override
    public Optional<TransactionResponseDTO> getTransactionDetails(Integer transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transactionMapper::toTransactionResponseDTO);
    }

    @Override
    public boolean checkForFraud(Transaction transaction) {
        // Implement fraud detection logic here (e.g., large transactions, unusual locations)
        // For now, return false as a placeholder
        return false;
    }

    @Override
    public void auditTransaction(Integer transactionId, Integer userId) {
        // You might log the transaction in an audit table or system
        logger.info("Audit transaction with ID: {} by user: {}", transactionId, userId);
    }

    @Override
    public void deleteTransaction(Integer transactionId) {
        transactionRepository.deleteById(transactionId);
    }
}