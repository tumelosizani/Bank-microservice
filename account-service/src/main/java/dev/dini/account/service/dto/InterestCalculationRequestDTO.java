package dev.dini.account.service.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterestCalculationRequestDTO {
    private UUID accountId;
}
