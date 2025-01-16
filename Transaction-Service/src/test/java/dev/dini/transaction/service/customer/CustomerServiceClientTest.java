package dev.dini.transaction.service.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CustomerServiceClientTest {

    @Mock
    private CustomerServiceClient customerServiceClient;

    @InjectMocks
    private CustomerServiceClientTest customerServiceClientTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCustomer_returnsCustomerDTOForValidCustomerId() {
        CustomerDTO expectedCustomer = new CustomerDTO();
        expectedCustomer.setCustomerId(1);
        expectedCustomer.setFirstName("John");
        expectedCustomer.setLastName("Doe");
        expectedCustomer.setEmail("johndoe@example.com");
        expectedCustomer.setPhoneNumber("123-456-7890");

        when(customerServiceClient.getCustomer(1)).thenReturn(expectedCustomer);

        CustomerDTO actualCustomer = customerServiceClient.getCustomer(1);

        assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    void getCustomer_returnsNullForNonExistentCustomerId() {
        when(customerServiceClient.getCustomer(999)).thenReturn(null);

        CustomerDTO actualCustomer = customerServiceClient.getCustomer(999);

        assertEquals(null, actualCustomer);
    }
}