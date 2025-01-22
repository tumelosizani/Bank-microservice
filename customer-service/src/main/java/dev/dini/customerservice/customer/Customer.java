package dev.dini.customerservice.customer;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID customerId;

    @NotNull
    private String firstname;
    @NotNull
    private String lastname;

    @Email
    private String email;


    private String phoneNumber;

    @Embedded
    private Address address; // assuming Address is an embeddable class

    @NotNull
    @Column(unique = true)
    private Integer idNumber; // National ID or Passport number

    @Enumerated(EnumType.STRING)
    private IdType idType; // National ID or Passport

    private String idDocumentUrl; // URL to ID document (e.g., scanned image)

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus; // Enum to track KYC status

    @Enumerated(EnumType.STRING)
    private CustomerStatus status; // Enum to track customer status

    private LocalDateTime createdAt; // Created timestamp
    private LocalDateTime updatedAt; // Updated timestamp

    @ElementCollection
    @CollectionTable(name = "customer_accounts", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "account_id")
    private List<UUID> accountId;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
