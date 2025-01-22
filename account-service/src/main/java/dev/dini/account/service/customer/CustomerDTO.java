package dev.dini.account.service.customer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
public class CustomerDTO {

    private UUID customerId;    // The unique ID of the customer
    private String firstName;      // The customer's first name
    private String lastName;       // The customer's last name
    private String email;          // The customer's email address
    private String phoneNumber;    // The customer's phone number
    private String address;        // The customer's address
}
