package dev.dini.account.service.interest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "interest_history")
public class InterestHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID accountId;
    private BigDecimal interestAmount;
    private LocalDateTime timestamp;

}
