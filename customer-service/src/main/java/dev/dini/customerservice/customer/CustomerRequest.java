package dev.dini.customerservice.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class CustomerRequest {

        private Integer customerId;

        @NotNull(message = "Customer firstname is required")
        private String firstname;
        @NotNull(message = "Customer firstname is required")
        private String lastname;
        @NotNull(message = "Customer Email is required")
        @Email(message = "Customer Email is not a valid email address")
        private String email;
        private Address address;

}
