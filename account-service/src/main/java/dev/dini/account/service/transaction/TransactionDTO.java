package dev.dini.account.service.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
}
