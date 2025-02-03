package dev.dini.customerservice.dto;

import dev.dini.customerservice.customer.IdType;
import dev.dini.customerservice.customer.KycStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateCustomerDTO {

    private String firstname;
    private String lastname;
    private String email;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Integer idNumber; // National ID or Passport number
    private IdType idType; // Enum to track ID type
    private String idDocumentUrl; // URL to ID document (e.g., scanned image)
    private String proofOfAddressUrl; // URL to proof of address document
    private KycStatus kycStatus; // Enum to track KYC status
    private List<UUID> accountIds; // List of account IDs associated with the customer
}