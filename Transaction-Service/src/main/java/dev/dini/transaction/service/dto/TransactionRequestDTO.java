package dev.dini.transaction.service.dto;

import dev.dini.transaction.service.transaction.PaymentMethod;
import dev.dini.transaction.service.transaction.TransactionType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class TransactionRequestDTO {
    private Integer senderAccountId;
    private Integer receiverAccountId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String description;
    private PaymentMethod paymentMethod;
    private Integer initiatedByCustomerId;
}