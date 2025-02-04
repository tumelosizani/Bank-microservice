package dev.dini.account.service.dto;

import dev.dini.account.service.account.AccountStatus;
import dev.dini.account.service.account.AccountType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequestDTO {

    private UUID customerId;           // The user ID associated with the account
    private String accountName;           // The name of the account
    private AccountType accountType; // The type of account (e.g., SAVINGS, CHECKING)
    private AccountStatus status;    // The status of the account (e.g., ACTIVE, INACTIVE)
    private boolean overdraftProtection;  // Whether the account has overdraft protection
    private Double transactionLimit;      // The transaction limit for the account
}

