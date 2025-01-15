package dev.dini.customerservice.customer;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue
    private Integer customerId;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;

    private Address address;
    private String idNumber; // National ID or Passport number
    private String idDocumentUrl; // URL to ID document (e.g., scanned image)
    private String proofOfAddressUrl; // URL to proof of address document
    private KycStatus kycStatus; // Enum to track KYC status

    private CustomerStatus status; // Enum to track customer status

    private LocalDateTime createdAt; // Created timestamp
    private LocalDateTime updatedAt; // Updated timestamp

    @ElementCollection
    @CollectionTable(name = "customer_accounts", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "account_id")
    private List<Integer> accountId;



    public Integer getCustomerId() {
        return customerId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}


