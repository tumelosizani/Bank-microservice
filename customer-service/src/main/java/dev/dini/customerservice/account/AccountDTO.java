package dev.dini.customerservice.account;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class AccountDTO {

    private UUID accountId;
    private String accountNumber;
    private String accountType;
    private Double balance;
    private String currency;
    private String status;
}
