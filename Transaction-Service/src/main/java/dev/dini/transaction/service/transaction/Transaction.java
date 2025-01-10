package dev.dini.transaction.service.transaction;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @NotNull
    @Column(name = "sender_account_id")
    private Integer senderAccountId;

    @NotNull
    @Column(name = "receiver_account_id")
    private Integer receiverAccountId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime timestamp;

    private String description;

    @Column(name = "transaction_reference_id", unique = true)
    private String transactionReferenceId;

    private PaymentMethod paymentMethod; // e.g., "Credit Card", "Wire Transfer", etc.

    private Integer initiatedByCustomerId; // User who initiated the transaction

    private Integer processedByCustomerId; // User who processed the transaction, for auditing
    
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // Lifecycle callback to set timestamp
    @PrePersist
    public void setTimestamp() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Lifecycle callback to update updatedAt
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
