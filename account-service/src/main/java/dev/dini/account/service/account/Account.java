package dev.dini.account.service.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID accountId;

    @NotNull
    private UUID customerId;

    private String accountName;
    private String accountNumber;

    @NotNull
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private boolean overdraftProtection;

    @NotNull
    private BigDecimal transactionLimit;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    private Integer version;

    @ElementCollection
    @CollectionTable(name = "account_holders", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "customer_id")
    private Set<UUID> accountHolders = new HashSet<>();


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addAccountHolder(UUID customerId) {
        this.accountHolders.add(customerId);
    }

    public void removeAccountHolder(UUID customerId) {
        this.accountHolders.remove(customerId);
    }

}
