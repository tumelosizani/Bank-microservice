package dev.dini.account.service.dto;

import dev.dini.account.service.account.AccountType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class AccountRequestDTO {
    private Integer customerId;
    private String accountName;
    private BigDecimal balance;
    private AccountType accountType;

}