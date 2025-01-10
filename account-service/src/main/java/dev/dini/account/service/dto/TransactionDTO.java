package dev.dini.account.service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class TransactionDTO {

    private Integer fromAccountId;
    private Integer toAccountId;
    private BigDecimal amount;
}
