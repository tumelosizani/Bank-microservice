package dev.dini.transaction.service.customer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CustomerDTO {

    private Integer customerId;
    private String email; // For notifications
    private String phoneNumber; // For notifications (optional)
}