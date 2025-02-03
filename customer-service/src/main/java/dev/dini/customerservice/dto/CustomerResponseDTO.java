package dev.dini.customerservice.dto;

import dev.dini.customerservice.account.AccountDTO;
import dev.dini.customerservice.customer.Address;
import dev.dini.customerservice.customer.CustomerStatus;
import dev.dini.customerservice.customer.KycStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerResponseDTO {

    private UUID customerId;
    private String firstname;
    private String lastname;
    private String email;

    private Address address; // Address embedded in DTO

    private String idNumber; // National ID or Passport number
    private CustomerStatus status;
    private KycStatus kycStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<AccountDTO> accounts; // Associated accounts
}
