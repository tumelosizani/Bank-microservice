package dev.dini.customerservice.customer;

import dev.dini.customerservice.account.AccountDTO;
import dev.dini.customerservice.account.AccountServiceClient;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import dev.dini.customerservice.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

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

        UUID customerId = UUID.randomUUID();
        when(customerService.createCustomer(any(CreateCustomerDTO.class))).thenReturn(customerId);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType("application/json")
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"" + customerId.toString() + "\""));

        verify(customerService, times(1)).createCustomer(any(CreateCustomerDTO.class));
    }

    @Test
    public void testGetCustomerById() throws Exception {
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setFirstname("John");
        customerResponseDTO.setLastname("Doe");

        UUID customerId = UUID.randomUUID();
        when(customerService.findById(customerId)).thenReturn(customerResponseDTO);

        mockMvc.perform(get("/api/v1/customers/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"));

        verify(customerService, times(1)).findById(customerId);
    }

    @Test
    public void testUpdateCustomerNotFound() throws Exception {
        UUID customerId = UUID.randomUUID();
        UpdateCustomerDTO updateCustomerDTO = new UpdateCustomerDTO();
        updateCustomerDTO.setFirstname("John");
        updateCustomerDTO.setLastname("Doe");

        when(customerService.existsByCustomerId(customerId)).thenReturn(false);
        doThrow(new CustomerNotFoundException("Customer with ID " + customerId + " not found")).when(customerService).updateCustomer(any(UpdateCustomerDTO.class));

        mockMvc.perform(put("/api/v1/customers/{customerId}", customerId)
                        .contentType("application/json")
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer with ID " + customerId + " not found"));

        verify(customerService, times(1)).existsByCustomerId(customerId);
        verify(customerService, times(0)).updateCustomer(any(UpdateCustomerDTO.class));
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(customerService.existsByCustomerId(customerId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/customers/{customerId}", customerId))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(customerId);
    }

    @Test
    public void testGetAccountForCustomer() throws Exception {
        UUID accountId = UUID.randomUUID();
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountId(accountId);

        UUID customerId = UUID.randomUUID();
        when(customerService.existsByCustomerId(customerId)).thenReturn(true);
        when(accountServiceClient.getAccountById(accountId)).thenReturn(accountDTO);

        mockMvc.perform(get("/api/v1/customers/{customerId}/accounts/{accountId}", customerId, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1));

        verify(accountServiceClient, times(1)).getAccountById(accountId);
    }

    @Test
    public void testAddFunds() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID receiverAccountId = UUID.randomUUID();
        when(customerService.existsByCustomerId(customerId)).thenReturn(true);

        mockMvc.perform(post("/api/v1/customers/{customerId}/accounts/{accountId}/add", customerId, 1)
                        .param("amount", "100.00"))
                .andExpect(status().isNoContent());

        verify(accountServiceClient, times(1)).addFunds(receiverAccountId, new BigDecimal("100.00"));
    }

    @Test
    public void testDeductFunds() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID senderAccountId = UUID.randomUUID();
        when(customerService.existsByCustomerId(customerId)).thenReturn(true);

        mockMvc.perform(post("/api/v1/customers/{customerId}/accounts/{accountId}/deduct", customerId, 1)
                        .param("amount", "50.00"))
                .andExpect(status().isNoContent());

        verify(accountServiceClient, times(1)).deductFunds(senderAccountId, new BigDecimal("50.00"));
    }

    @Test
    public void testActivateCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(customerService.existsByCustomerId(customerId)).thenReturn(true);

        mockMvc.perform(put("/api/v1/customers/{customerId}/activate", customerId))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).activateCustomer(customerId);
    }

    @Test
    public void testDeactivateCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(customerService.existsByCustomerId(customerId)).thenReturn(true);

        mockMvc.perform(put("/api/v1/customers/{customerId}/deactivate", customerId))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deactivateCustomer(customerId);
    }
}