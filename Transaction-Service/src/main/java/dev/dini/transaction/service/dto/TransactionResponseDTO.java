package dev.dini.transaction.service.dto;

import dev.dini.transaction.service.transaction.PaymentMethod;
import dev.dini.transaction.service.transaction.TransactionStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class TransactionResponseDTO {
    private Integer transactionId;
    private Integer senderAccountId;
    private Integer receiverAccountId;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime timestamp;
    private String description;
    private String transactionReferenceId;
    private PaymentMethod paymentMethod;
    private Integer initiatedByCustomerId;
    private Integer processedByCustomerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}