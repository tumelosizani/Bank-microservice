package dev.dini.account.service.dto;

import dev.dini.account.service.account.AccountType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Data
public class AccountResponseDTO {
    private UUID accountId;
    private UUID customerId;
    private String accountName;
    private BigDecimal balance;
    private AccountType accountType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}