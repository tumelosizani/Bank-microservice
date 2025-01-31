package dev.dini.account.service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
public class InterestCalculationRequestDTO {
    private UUID accountId;
}
