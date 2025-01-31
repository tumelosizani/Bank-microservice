package dev.dini.account.service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Data
public class InterestCalculationResponseDTO {
    private UUID accountId;
    private BigDecimal interestAmount;
}
