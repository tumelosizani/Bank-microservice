package dev.dini.account.service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
public class FundTransferRequestDTO {
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
}
