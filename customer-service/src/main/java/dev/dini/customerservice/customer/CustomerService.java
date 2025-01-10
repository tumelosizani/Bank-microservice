package dev.dini.customerservice.customer;

import dev.dini.customerservice.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    String createCustomer(CustomerRequest request);

    void updateCustomer(CustomerRequest request);

    List<CustomerResponseDTO> findAllCustomers();

    CustomerResponseDTO findById(Integer customerId);

    boolean existsById(Integer customerId);

    void deleteCustomer(Integer customerId);
}
