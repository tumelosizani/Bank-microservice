package dev.dini.transaction.service.transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findBySenderAccountIdOrReceiverAccountId(Integer accountId, Integer accountId1);
}
