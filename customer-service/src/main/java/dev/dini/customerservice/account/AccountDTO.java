package dev.dini.customerservice.account;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AccountDTO {

    private Integer accountId;
    private String accountNumber;
    private String accountType;
    private Double balance;
    private String currency;
    private String status;
}
