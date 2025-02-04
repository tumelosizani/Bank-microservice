package dev.dini.account.service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestCalculationResponseDTO {
    private UUID accountId;
    private BigDecimal interestAmount;
}
