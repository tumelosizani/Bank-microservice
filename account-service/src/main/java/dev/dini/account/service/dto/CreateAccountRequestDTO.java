package dev.dini.account.service.dto;

import dev.dini.account.service.account.AccountType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CreateAccountRequestDTO {

    private Integer userId;           // The user ID associated with the account
    private AccountType accountType; // The type of account (e.g., SAVINGS, CHECKING)
}

