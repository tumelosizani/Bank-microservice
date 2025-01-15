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
    public ResponseEntity<Integer> createCustomer(@RequestBody @Valid CreateCustomerDTO createCustomerDTO) {
        Integer customerId = customerService.createCustomer(createCustomerDTO);
        return new ResponseEntity<>(customerId, HttpStatus.CREATED);
    }

    // Update an existing customer
    @PutMapping("/{customerId}")
    public ResponseEntity<Void> updateCustomer(@PathVariable Integer customerId,
                                               @RequestBody @Valid UpdateCustomerDTO updateCustomerDTO) {
        if (customerService.existsByCustomerId(customerId)) {
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
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Integer customerId) {
        CustomerResponseDTO customer = customerService.findById(customerId);
        return ResponseEntity.ok(customer);
    }

    // Delete a customer
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        if (customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build(); // No content as the response
    }

    // Get account details for a customer
    @GetMapping("/{customerId}/accounts/{accountId}")
    public ResponseEntity<AccountDTO> getAccountForCustomer(@PathVariable Integer customerId, @PathVariable Integer accountId) {
        if (customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        AccountDTO accountDTO = accountServiceClient.getAccount(accountId);
        return ResponseEntity.ok(accountDTO);
    }

    // Deduct funds from a customer's account
    @PostMapping("/{customerId}/accounts/{accountId}/deduct")
    public ResponseEntity<Void> deductFunds(@PathVariable Integer customerId, @PathVariable Integer accountId, @RequestParam BigDecimal amount) {
        if (customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        accountServiceClient.deductFunds(accountId, amount);
        return ResponseEntity.noContent().build();
    }

    // Add funds to a customer's account
    @PostMapping("/{customerId}/accounts/{accountId}/add")
    public ResponseEntity<Void> addFunds(@PathVariable Integer customerId, @PathVariable Integer accountId, @RequestParam BigDecimal amount) {
        if (customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        accountServiceClient.addFunds(accountId, amount);
        return ResponseEntity.noContent().build();
    }

    // Deactivate a customer
    @PutMapping("/{customerId}/deactivate")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable Integer customerId) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerService.deactivateCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    // Activate a customer
    @PutMapping("/{customerId}/activate")
    public ResponseEntity<Void> activateCustomer(@PathVariable Integer customerId) {
        if (!customerService.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerService.activateCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    // Suspend a customer
    @PutMapping("/{customerId}/suspend")
    public ResponseEntity<Void> suspendCustomer(@PathVariable Integer customerId) {
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