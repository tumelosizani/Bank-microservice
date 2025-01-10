package dev.dini.Transaction_Service.transaction;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateTransactionRequest {

    @NotNull
    private Integer accountId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private TransactionType transactionType;

    private String description;

    // Default constructor
    public CreateTransactionRequest() {}

    // Constructor with parameters
    public CreateTransactionRequest(Integer accountId, BigDecimal amount, TransactionType transactionType, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
    }

    // Getters and Setters

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // Corrected getter method for transactionType
    public TransactionType getTransactionType() {
        return transactionType; // Removed the unnecessary parameter
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // toString method (optional, for easier logging or debugging)
    @Override
    public String toString() {
        return "CreateTransactionRequest{" +
                "accountId=" + accountId +
                ", amount=" + amount +
                ", transactionType='" + transactionType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
