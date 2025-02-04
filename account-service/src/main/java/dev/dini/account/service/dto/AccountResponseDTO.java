package dev.dini.account.service.dto;

import dev.dini.account.service.account.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDTO {
    private UUID accountId;
    private UUID customerId;
    private String accountName;
    private BigDecimal balance;
    private AccountType accountType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}