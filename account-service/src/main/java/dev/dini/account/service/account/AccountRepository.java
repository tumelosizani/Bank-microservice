package dev.dini.account.service.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findByCustomerId(Integer customerId);
    Optional<Account> findByAccountId(Integer accountId);
}

