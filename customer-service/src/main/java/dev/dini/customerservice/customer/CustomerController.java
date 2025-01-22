package dev.dini.customerservice.customer;

import dev.dini.customerservice.account.AccountDTO;
import dev.dini.customerservice.account.AccountServiceClient;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import dev.dini.customerservice.exception.CustomerNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final AccountServiceClient accountServiceClient;

    public CustomerController(CustomerService customerService, AccountServiceClient accountServiceClient) {
        this.customerService = customerService;
        this.accountServiceClient = accountServiceClient;
    }

    // Create a new customer
    @PostMapping
    public ResponseEntity<UUID> createCustomer(@RequestBody @Valid CreateCustomerDTO createCustomerDTO) {
        UUID customerId = customerService.createCustomer(createCustomerDTO);
        return new ResponseEntity<>(customerId, HttpStatus.CREATED);
    }

    // Update an existing customer
    @PutMapping("/{customerId}")
    public ResponseEntity<Void> updateCustomer(@PathVariable UUID customerId,
                                               @RequestBody @Valid UpdateCustomerDTO updateCustomerDTO) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        updateCustomerDTO.setCustomerId(customerId);
        customerService.updateCustomer(updateCustomerDTO);
        return ResponseEntity.noContent().build(); // No content as the response
    }

    // Get all customers
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    // Get a customer by ID
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable UUID customerId) {
        CustomerResponseDTO customer = customerService.findById(customerId);
        return ResponseEntity.ok(customer);
    }

    // Delete a customer
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID customerId) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build(); // No content as the response
    }

    // Get account details for a customer
    @GetMapping("/{customerId}/accounts/{accountId}")
    public ResponseEntity<AccountDTO> getAccountForCustomer(@PathVariable UUID customerId, @PathVariable UUID accountId) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        AccountDTO accountDTO = accountServiceClient.getAccountById(accountId);
        return ResponseEntity.ok(accountDTO);
    }

    // Get all accounts for a customer
    @GetMapping("/{customerId}/accounts")
    public ResponseEntity<List<AccountDTO>> getAccountsForCustomer(@PathVariable UUID customerId) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        List<AccountDTO> accountDTOs = accountServiceClient.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accountDTOs);
    }

    // Deduct funds from a customer's account
    @PostMapping("/{customerId}/accounts/{accountId}/deduct")
    public ResponseEntity<Void> deductFunds(@PathVariable UUID customerId, @PathVariable UUID accountId, @RequestParam BigDecimal amount) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        accountServiceClient.deductFunds(accountId, amount);
        return ResponseEntity.noContent().build();
    }

    // Add funds to a customer's account
    @PostMapping("/{customerId}/accounts/{accountId}/add")
    public ResponseEntity<Void> addFunds(@PathVariable UUID customerId, @PathVariable UUID accountId, @RequestParam BigDecimal amount) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        accountServiceClient.addFunds(accountId, amount);
        return ResponseEntity.noContent().build();
    }

    // Deactivate a customer
    @PutMapping("/{customerId}/deactivate")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable UUID customerId) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerService.deactivateCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    // Activate a customer
    @PutMapping("/{customerId}/activate")
    public ResponseEntity<Void> activateCustomer(@PathVariable UUID customerId) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerService.activateCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    // Suspend a customer
    @PutMapping("/{customerId}/suspend")
    public ResponseEntity<Void> suspendCustomer(@PathVariable UUID customerId) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerService.suspendCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    // Get a customer by email
    @GetMapping("/email")
    public ResponseEntity<CustomerResponseDTO> getCustomerByEmail(@RequestParam String email) {
        CustomerResponseDTO customer = customerService.findByEmail(email);
        return ResponseEntity.ok(customer);
    }

    // Get a customer by phone number
    @GetMapping("/phone")
    public ResponseEntity<CustomerResponseDTO> getCustomerByPhoneNumber(@RequestParam String phoneNumber) {
        CustomerResponseDTO customer = customerService.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(customer);
    }

    // Check if a customer exists by email
    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        boolean exists = customerService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    // Check if a customer exists by phone number
    @GetMapping("/exists/phone")
    public ResponseEntity<Boolean> existsByPhoneNumber(@RequestParam String phoneNumber) {
        boolean exists = customerService.existsByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(exists);
    }
}