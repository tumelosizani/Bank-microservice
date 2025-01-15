package dev.dini.account.service.customer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CustomerDTO {

    private Integer customerId;    // The unique ID of the customer
    private String firstName;      // The customer's first name
    private String lastName;       // The customer's last name
    private String email;          // The customer's email address
    private String phoneNumber;    // The customer's phone number
    private String address;        // The customer's address
}
