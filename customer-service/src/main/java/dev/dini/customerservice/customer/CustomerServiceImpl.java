package dev.dini.customerservice.customer;

import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import dev.dini.customerservice.exception.CustomerNotFoundException;
import dev.dini.customerservice.mapper.CustomerMapper;
import jakarta.validation.Valid;
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
        Customer customer = this.customerRepository.findByCustomerId(updateCustomerDTO.getCustomerId())
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
                .map(customerMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO findById(Integer customerId) {
        return this.customerRepository.findById(customerId)
                .map(customerMapper::toResponseDTO)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found with the provided ID: %s", customerId)));
    }

    @Override
    public boolean existsByCustomerId(Integer customerId) {
        return this.customerRepository.findById(customerId).isPresent();
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        this.customerRepository.deleteById(customerId);
    }

    @Override
    public void deactivateCustomer(Integer customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found with the provided ID: %s", customerId)));
        customer.setStatus(CustomerStatus.DEACTIVATED);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    public void activateCustomer(Integer customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found with the provided ID: %s", customerId)));
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    public void suspendCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("No customer found with the provided ID: " + customerId));
        customer.setStatus(CustomerStatus.SUSPENDED);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.customerRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return this.customerRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public CustomerResponseDTO findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(customer -> customerMapper.toResponseDTO((Customer) customer))
                .orElseThrow(() -> new CustomerNotFoundException("No customer found with the provided email: " + email));
    }

    @Override
    public CustomerResponseDTO findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(customer -> customerMapper.toResponseDTO((Customer) customer))
                .orElseThrow(() -> new CustomerNotFoundException("No customer found with the provided phone number: " + phoneNumber));
    }
}