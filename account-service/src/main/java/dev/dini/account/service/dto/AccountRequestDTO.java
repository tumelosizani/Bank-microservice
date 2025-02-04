package dev.dini.account.service.dto;

import dev.dini.account.service.account.AccountType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Data
public class AccountRequestDTO {
    private UUID customerId;
    private String accountName;
    private BigDecimal balance;
    private AccountType accountType;

}