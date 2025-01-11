package dev.dini.customerservice.dto;

import dev.dini.customerservice.customer.Address;
import dev.dini.customerservice.customer.KycStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class UpdateCustomerDTO {

    private Integer customerId;
    private String firstname;
    private String lastname;
    private String email;
    private Address address;
    private String idNumber; // National ID or Passport number
    private String idDocumentUrl; // URL to ID document (e.g., scanned image)
    private String proofOfAddressUrl; // URL to proof of address document
    private KycStatus kycStatus; // Enum to track KYC status

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
