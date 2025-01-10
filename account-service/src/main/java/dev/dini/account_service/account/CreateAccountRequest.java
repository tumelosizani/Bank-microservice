package dev.dini.account_service.account;


public class CreateAccountRequest {

    private Integer userId;           // The user ID associated with the account
    private AccountType accountType; // The type of account (e.g., SAVINGS, CHECKING)

    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}

