package dev.dini.account.service.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CustomerServiceClientTest {

    @Mock
    private CustomerServiceClient customerServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCustomerById_withValidId_returnsCustomer() {
        UUID customerId = UUID.randomUUID();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId(customerId);
        when(customerServiceClient.getCustomerById(any(UUID.class))).thenReturn(customerDTO);

        CustomerDTO result = customerServiceClient.getCustomerById(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), result.getCustomerId());
    }

    @Test
    void getCustomerById_withInvalidId_returnsNull() {
        UUID customerId = UUID.randomUUID();
        when(customerServiceClient.getCustomerById(any(UUID.class))).thenReturn(null);

        CustomerDTO result = customerServiceClient.getCustomerById(customerId);

        assertEquals(null, result);
    }
}