package dev.dini.customerservice.dto;

import dev.dini.customerservice.customer.Address;

public record CustomerResponseDTO(
        Integer customerId,
        String firstname,
        String lastname,
        String email,
        Address address
) {

}
