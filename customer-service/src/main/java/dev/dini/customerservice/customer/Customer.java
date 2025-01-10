package dev.dini.customerservice.customer;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue
    private String customerId;
    private String firstname;
    private String lastname;
    private String email;

    private Address address;
    private String idNumber; // National ID or Passport number
    private String idDocumentUrl; // URL to ID document (e.g., scanned image)
    private String proofOfAddressUrl; // URL to proof of address document
    private KycStatus kycStatus; // Enum to track KYC status
}


