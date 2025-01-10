package dev.dini.account.service.dto;

import dev.dini.account.service.account.AccountType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class AccountResponseDTO {
    private Integer accountId;
    private Integer userId;
    private String accountName;
    private BigDecimal balance;
    private AccountType accountType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}