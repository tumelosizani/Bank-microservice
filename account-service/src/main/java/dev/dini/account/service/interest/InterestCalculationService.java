package dev.dini.account.service.interest;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.account.AccountType;
import dev.dini.account.service.notification.AccountNotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(InterestCalculationService.class);

    private final AccountRepository accountRepository;
    private final AccountNotificationService accountNotificationService;
    private final InterestHistoryRepository interestHistoryRepository;

    @Value("${interest.rate}")
    private BigDecimal interestRate;

    // Helper method to calculate interest
    private BigDecimal calculateInterestAmount(Account account) {
        if (account.getBalance() != null) {
            return account.getBalance().multiply(interestRate);
        } else {
            logger.warn("Account ID: {} has a null balance", account.getAccountId());
            return BigDecimal.ZERO;
        }
    }

    @Scheduled(cron = "0 0 0 1 * ?") // Runs every month
    public void applyInterest() {
        logger.info("Applying interest to all eligible accounts");

        List<Account> savingsAccounts = accountRepository.findByAccountType(AccountType.SAVINGS);
        for (Account account : savingsAccounts) {
            try {
                BigDecimal interest = calculateInterestAmount(account).divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
                BigDecimal oldBalance = account.getBalance();
                BigDecimal newBalance = oldBalance.add(interest);

                // Log before updating the account
                logInterestHistory(account, interest);

                account.setBalance(newBalance);
                accountRepository.save(account);

                // Notify the customer
                accountNotificationService.sendInterestAppliedNotification(account, interest);

                logger.info("Interest applied to Account ID: {}, Old Balance: {}, Interest: {}, New Balance: {}",
                        account.getAccountId(), oldBalance, interest, newBalance);
            } catch (Exception e) {
                logger.error("Error applying interest to Account ID: {}", account.getAccountId(), e);
            }
        }
    }


    // Reuse the helper method in the normal interest calculation
    public BigDecimal calculateInterest(Account account) {
        return calculateInterestAmount(account);
    }

    public void logInterestHistory(Account account, BigDecimal interestAmount) {
        InterestHistory interestHistory = new InterestHistory();
        interestHistory.setAccountId(account.getAccountId());  // Use account ID (UUID)
        interestHistory.setInterestAmount(interestAmount);
        interestHistory.setTimestamp(LocalDateTime.now());
        interestHistoryRepository.save(interestHistory);
    }


    private BigDecimal calculateCompoundInterestAmount(Account account) {
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
        return account.getBalance().multiply(monthlyRate);
    }

    private BigDecimal getDynamicInterestRate(Account account) {
        if (account.getBalance().compareTo(new BigDecimal("10000")) > 0) {
            return new BigDecimal("0.05"); // 5% for high-balance accounts
        } else {
            return interestRate; // Default interest rate
        }
    }

    public BigDecimal previewInterestApplication(Account account) {
        return calculateInterestAmount(account).divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
    }


}
