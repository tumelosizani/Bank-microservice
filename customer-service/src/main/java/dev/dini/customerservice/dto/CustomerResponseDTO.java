package dev.dini.customerservice.dto;

import dev.dini.customerservice.customer.Address;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CustomerResponseDTO {
    private Integer customerId;
    private String firstname;
    private String lastname;
    private String email;
    private Address address;

}
