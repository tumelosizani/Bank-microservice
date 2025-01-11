package dev.dini.customerservice.customer;

import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import dev.dini.customerservice.exception.CustomerNotFoundException;
import dev.dini.customerservice.mapper.CustomerMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public Integer createCustomer(@Valid CreateCustomerDTO createCustomerDTO) {
        Customer customer = customerMapper.toEntity(createCustomerDTO);
        customer.setCreatedAt(LocalDateTime.now());
        customer = this.customerRepository.save(customer);
        return customer.getCustomerId();
    }

    @Override
    public void updateCustomer(UpdateCustomerDTO updateCustomerDTO) {
        Customer customer = (Customer) this.customerRepository.findByCustomerId(updateCustomerDTO.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Cannot update customer:: No customer found with the provided ID: %s", updateCustomerDTO.getCustomerId())
                ));
        customerMapper.updateCustomerFromDto(updateCustomerDTO, customer);
        customer.setUpdatedAt(LocalDateTime.now());
        this.customerRepository.save(customer);
    }

    @Override
    public List<CustomerResponseDTO> findAllCustomers() {
        return this.customerRepository.findAll()
                .stream()
                .map(this.customerMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO findById(Integer customerId) {
        return this.customerRepository.findById(customerId)
                .map(customerMapper::toResponseDTO)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found with the provided ID: %s", customerId)));
    }

    @Override
    public boolean existsById(Integer customerId) {
        return this.customerRepository.findById(customerId)
                .isPresent();
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        this.customerRepository.deleteById(customerId);
    }
}