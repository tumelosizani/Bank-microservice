package dev.dini.account.service.component;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.account.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InterestCalculation {

    private final AccountRepository accountRepository;
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.02"); // 2% annual interest

    @Scheduled(cron = "0 0 0 1 * ?") // Runs every month
    public void applyInterest() {
        List<Account> savingsAccounts = accountRepository.findByAccountType(AccountType.SAVINGS);
        for (Account account : savingsAccounts) {
            BigDecimal interest = account.getBalance().multiply(INTEREST_RATE.divide(new BigDecimal("12")));
            account.setBalance(account.getBalance().add(interest));
            accountRepository.save(account);
        }
    }

    public BigDecimal calculateInterest(Account account) {
        return account.getBalance().multiply(INTEREST_RATE);
    }
}
