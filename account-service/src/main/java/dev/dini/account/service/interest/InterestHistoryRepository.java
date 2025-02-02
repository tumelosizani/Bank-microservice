package dev.dini.account.service.interest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterestHistoryRepository extends JpaRepository<InterestHistory, Long> {
    List<InterestHistory> findByAccountId(UUID accountId);
}
