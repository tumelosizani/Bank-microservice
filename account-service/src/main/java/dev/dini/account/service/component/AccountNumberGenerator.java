package dev.dini.account.service.component;

import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.account.AccountType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AccountNumberGenerator.class);
    private final AccountRepository accountRepository;

    // Method to encode the account type into the account number
    private String encodeAccountType(AccountType accountType) {
        switch (accountType) {
            case  STUDENT:
                return "ST"; // Student-account
            case BUSINESS:
                return "B"; // Business-account
            case SAVINGS:
                return "S"; // Savings-account
            case CHECKING:
                return "C"; // Checking-account
            default:
                return "U"; // Unknown or undefined account type
        }
    }

    // Method to generate a random 3-4 digit branch or region code (e.g., based on the branch)
    private String generateBranchCode() {
        int branchCode = ThreadLocalRandom.current().nextInt(100, 1000); // 3-digit branch code
        return String.format("%03d", branchCode);
    }

    // Luhn algorithm to generate check digit for account number validation
    private int generateCheckDigit(String accountNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = accountNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(accountNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }

    // Generates a random account number with the account type, branch code, and check digit
    public String generateAccountNumber(AccountType accountType) {
        String accountTypeCode = encodeAccountType(accountType);
        String branchCode = generateBranchCode();
        int randomNumber = ThreadLocalRandom.current().nextInt(100000, 999999); // 6-digit random number
        String partialAccountNumber = accountTypeCode + "-" + branchCode + "-" + randomNumber;

        // Generate check digit and append to account number
        int checkDigit = generateCheckDigit(partialAccountNumber.replace("-", ""));
        return partialAccountNumber + "-" + checkDigit;
    }

    // Ensures the generated account number is unique by checking the repository
    public String generateUniqueAccountNumber(AccountType accountType) {
        String accountNumber;
        int attempts = 0;

        do {
            accountNumber = generateAccountNumber(accountType);
            attempts++;

            // Break if we've tried too many times to avoid infinite loops
            if (attempts > 100) {
                logger.error("Unable to generate a unique account number after 100 attempts.");
                throw new RuntimeException("Unable to generate a unique account number.");
            }

        } while (accountRepository.existsByAccountNumber(accountNumber));

        logger.info("Generated unique account number: {}", accountNumber);
        return accountNumber;
    }
}
