package dev.dini.customerservice.customer;

import dev.dini.customerservice.account.AccountDTO;
import dev.dini.customerservice.account.AccountServiceClient;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(customerController)
                .build();
    }

    @Test
    public void testCreateCustomer() throws Exception {
        CreateCustomerDTO createCustomerDTO = new CreateCustomerDTO();
        createCustomerDTO.setFirstname("John");
        createCustomerDTO.setLastname("Doe");

        when(customerService.createCustomer(any(CreateCustomerDTO.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType("application/json")
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(customerService, times(1)).createCustomer(any(CreateCustomerDTO.class));
    }

    @Test
    public void testGetCustomerById() throws Exception {
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setFirstname("John");
        customerResponseDTO.setLastname("Doe");

        when(customerService.findById(anyInt())).thenReturn(customerResponseDTO);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"));

        verify(customerService, times(1)).findById(1);
    }

    @Test
    public void testUpdateCustomerNotFound() throws Exception {
        Integer customerId = 1;
        UpdateCustomerDTO updateCustomerDTO = new UpdateCustomerDTO();
        updateCustomerDTO.setFirstname("John");
        updateCustomerDTO.setLastname("Doe");

        // Mock the service to return false for the exists check
        when(customerService.existsByCustomerId(customerId)).thenReturn(false);

        // Perform the PUT request and expect the 404 Not Found status
        mockMvc.perform(put("/api/v1/customers/{customerId}", customerId)
                        .contentType("application/json")
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer with ID 1 not found"));

        // Verify the interactions with customerService
        verify(customerService, times(1)).existsByCustomerId(customerId);
        verify(customerService, times(0)).updateCustomer(any(UpdateCustomerDTO.class)); // Ensure update is not called
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        when(customerService.existsByCustomerId(anyInt())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(1);
    }

    @Test
    public void testGetAccountForCustomer() throws Exception {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountId(1);

        when(customerService.existsByCustomerId(anyInt())).thenReturn(true);
        when(accountServiceClient.getAccountById(anyInt())).thenReturn(accountDTO);

        mockMvc.perform(get("/api/v1/customers/1/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1));

        verify(accountServiceClient, times(1)).getAccountById(anyInt());
    }

    @Test
    public void testAddFunds() throws Exception {
        when(customerService.existsByCustomerId(anyInt())).thenReturn(true);

        mockMvc.perform(post("/api/v1/customers/1/accounts/1/add")
                        .param("amount", "100.00"))
                .andExpect(status().isNoContent());

        verify(accountServiceClient, times(1)).addFunds(anyInt(), any());
    }

    @Test
    public void testDeductFunds() throws Exception {
        when(customerService.existsByCustomerId(anyInt())).thenReturn(true);

        mockMvc.perform(post("/api/v1/customers/1/accounts/1/deduct")
                        .param("amount", "50.00"))
                .andExpect(status().isNoContent());

        verify(accountServiceClient, times(1)).deductFunds(anyInt(), any());
    }

    @Test
    public void testActivateCustomer() throws Exception {
        when(customerService.existsByCustomerId(anyInt())).thenReturn(true);

        mockMvc.perform(put("/api/v1/customers/1/activate"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).activateCustomer(1);
    }

    @Test
    public void testDeactivateCustomer() throws Exception {
        when(customerService.existsByCustomerId(anyInt())).thenReturn(true);

        mockMvc.perform(put("/api/v1/customers/1/deactivate"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deactivateCustomer(1);
    }
}