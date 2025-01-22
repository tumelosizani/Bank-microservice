package dev.dini.customerservice.customer;

import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    UUID createCustomer(@Valid CreateCustomerDTO createCustomerDTO);

    void updateCustomer(UpdateCustomerDTO updateCustomerDTO);

    List<CustomerResponseDTO> findAllCustomers();

    CustomerResponseDTO findById(UUID customerId);

    boolean existsByCustomerId(UUID customerId);

    void deleteCustomer(UUID customerId);

    void deactivateCustomer(UUID customerId);

    void activateCustomer(UUID customerId);

    void suspendCustomer(UUID customerId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    CustomerResponseDTO findByEmail(String email);

    CustomerResponseDTO findByPhoneNumber(String phoneNumber);


}
