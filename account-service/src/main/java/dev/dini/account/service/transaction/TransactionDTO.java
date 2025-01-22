package dev.dini.account.service.transaction;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Data
public class TransactionDTO {

    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
}
