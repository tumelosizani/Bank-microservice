package dev.dini.customerservice.customer;

import dev.dini.customerservice.account.AccountDTO;
import dev.dini.customerservice.account.AccountServiceClient;
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
    private final AccountServiceClient accountServiceClient;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper, AccountServiceClient accountServiceClient) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.accountServiceClient = accountServiceClient;
    }

    @Override
    public Integer createCustomer(@Valid CreateCustomerDTO createCustomerDTO) {
        // Map DTO to Customer Entity
        Customer customer = customerMapper.toEntity(createCustomerDTO);
        customer.setCreatedAt(LocalDateTime.now());
        customer = this.customerRepository.save(customer);

        // Call Account Service to link accounts
        final Customer savedCustomer = customer;
        if (createCustomerDTO.getAccountIds() != null && !createCustomerDTO.getAccountIds().isEmpty()) {
            createCustomerDTO.getAccountIds().forEach(accountId ->
                    accountServiceClient.linkAccountToCustomer(accountId, savedCustomer.getCustomerId())
            );
        }

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
        Customer customer = this.customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("No customer found with ID: " + customerId));

        List<AccountDTO> accounts = accountServiceClient.getAccountsByCustomerId(customerId);

        CustomerResponseDTO response = customerMapper.toResponseDTO(customer);
        response.setAccounts(accounts); // Set accounts in response
        return response;
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
                .orElseThrow(() -> new CustomerNotFoundException("No customer found with the provided ID: " + customerId));
        customer.setStatus(CustomerStatus.DEACTIVATED);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);

        // Optionally update the account status
        if (customer.getAccountId() != null) {
            AccountDTO account = accountServiceClient.getAccountById(customer.getAccountId());
            if (account != null) {
                account.setStatus("DEACTIVATED");
                // Save the account update via another service call if necessary
            }
        }
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