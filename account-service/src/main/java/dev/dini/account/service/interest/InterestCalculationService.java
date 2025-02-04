package dev.dini.account.service.interest;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.account.AccountType;
import dev.dini.account.service.dto.InterestCalculationRequestDTO;
import dev.dini.account.service.dto.InterestCalculationResponseDTO;
import dev.dini.account.service.exception.AccountNotFoundException;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterestCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(InterestCalculationService.class);

    private final AccountRepository accountRepository;
    private final AccountNotificationService accountNotificationService;
    private final InterestHistoryRepository interestHistoryRepository;

    @Value("${interest.rate}")
    private BigDecimal interestRate;

    public InterestCalculationResponseDTO calculateInterest(InterestCalculationRequestDTO request) {
        UUID accountId = request.getAccountId();
        logger.info("Calculating interest for account ID: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        BigDecimal interestRate = getInterestRateForAccount(account);
        BigDecimal interestAmount = account.getBalance().multiply(interestRate);

        // Updating the account balance with the calculated interest
        account.setBalance(account.getBalance().add(interestAmount));
        accountRepository.save(account);

        logger.info("Interest calculated successfully for account ID: {}. Interest amount: {}", accountId, interestAmount);

        // Returning the response DTO
        return new InterestCalculationResponseDTO(accountId, interestAmount);
    }

    private BigDecimal getInterestRateForAccount(Account account) {
        // Example logic for determining interest rate based on account type
        switch (account.getAccountType()) {
            case SAVINGS:
                return new BigDecimal("0.03"); // 3% interest rate for savings accounts
            case CHECKING:
                return new BigDecimal("0.01"); // 1% interest rate for checking accounts
            default:
                return BigDecimal.ZERO;
        }
    }

    // Helper method to calculate interest
    private BigDecimal calculateInterestAmount(Account account) {
        if (account.getBalance() != null && interestRate != null) {
            return account.getBalance().multiply(interestRate);
        } else {
            logger.warn("Account ID: {} has a null balance or interest rate", account.getAccountId());
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