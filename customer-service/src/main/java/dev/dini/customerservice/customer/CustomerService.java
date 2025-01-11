package dev.dini.customerservice.customer;

import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface CustomerService {

    Integer createCustomer(@Valid CreateCustomerDTO createCustomerDTO);

    void updateCustomer(UpdateCustomerDTO updateCustomerDTO);


    List<CustomerResponseDTO> findAllCustomers();

    CustomerResponseDTO findById(Integer customerId);

    boolean existsById(Integer customerId);

    void deleteCustomer(Integer customerId);
}
