package dev.dini.account.service.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByCustomerId(UUID customerId);
    Optional<Account> findByAccountId(UUID accountId);

    List<Account> findByAccountType(AccountType accountType);

    boolean existsByAccountNumber(String accountNumber);
}

