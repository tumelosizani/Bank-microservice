package dev.dini.customerservice.dto;

import dev.dini.customerservice.customer.KycStatus;
import lombok.*;

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
    private String idNumber; // National ID or Passport number
    private String idDocumentUrl; // URL to ID document (e.g., scanned image)
    private String proofOfAddressUrl; // URL to proof of address document
    private KycStatus kycStatus; // Enum to track KYC status
}