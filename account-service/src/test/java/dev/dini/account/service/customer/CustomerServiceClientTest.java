package dev.dini.account.service.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
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
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId(1);
        when(customerServiceClient.getCustomerById(anyInt())).thenReturn(customerDTO);

        CustomerDTO result = customerServiceClient.getCustomerById(1);

        assertEquals(1, result.getCustomerId());
    }

    @Test
    void getCustomerById_withInvalidId_returnsNull() {
        when(customerServiceClient.getCustomerById(anyInt())).thenReturn(null);

        CustomerDTO result = customerServiceClient.getCustomerById(999);

        assertEquals(null, result);
    }
}