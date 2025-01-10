package dev.dini.Transaction_Service.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    // You can add custom query methods here if necessary
}
