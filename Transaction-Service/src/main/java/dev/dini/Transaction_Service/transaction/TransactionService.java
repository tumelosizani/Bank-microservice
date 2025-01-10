package dev.dini.Transaction_Service.transaction;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransactionService {


    @Transactional
    Transaction createTransaction(Integer accountId, BigDecimal amount, TransactionType transactionType, String description);

    @Transactional
    Optional<Transaction> getTransaction(Integer transactionId);


    @Transactional
    Transaction completeTransaction(Integer transactionId);

    @Transactional
    Transaction cancelTransaction(Integer transactionId);

    @Transactional
    void deleteTransaction(Integer transactionId);
}
