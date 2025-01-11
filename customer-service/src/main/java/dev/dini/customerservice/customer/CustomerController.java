package dev.dini.customerservice.customer;

import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import dev.dini.customerservice.exception.CustomerNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
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
        // You may want to check if the customer exists first or include the ID in the update DTO
        if (!customerService.existsById(customerId)) {
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
        if (!customerService.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build(); // No content as the response
    }
}
