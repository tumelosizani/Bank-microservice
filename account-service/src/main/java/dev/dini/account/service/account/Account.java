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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer accountId;

    @NotNull
    private Integer customerId;

    private String accountName;

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
    private Set<Integer> accountHolders = new HashSet<>();


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addAccountHolder(Integer customerId) {
        this.accountHolders.add(customerId);
    }

    public void removeAccountHolder(Integer customerId) {
        this.accountHolders.remove(customerId);
    }

}
