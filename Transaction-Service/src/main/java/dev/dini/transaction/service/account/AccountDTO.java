package dev.dini.transaction.service.account;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class AccountDTO {
    private Integer accountId;
    private Integer accountNumber;
    private String accountHolderName;
    private String accountType;
    private BigDecimal balance;
}
