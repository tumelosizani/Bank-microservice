package dev.dini.account.service.overdraft;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountType;
import org.springframework.stereotype.Component;

@Component
public class OverdraftProtectionValidator {

    /**
     * Checks if the account type supports overdraft protection.
     *
     * @param account The account whose type we want to check.
     * @return true if overdraft protection is allowed, false otherwise.
     */
    public boolean isOverdraftProtectionAllowed(Account account) {
        // Example logic: Only "CHECKING" type accounts can have overdraft protection
        return AccountType.CHECKING.equals(account.getAccountType());
    }
}
