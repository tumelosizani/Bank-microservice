package dev.dini.customerservice.customer;

import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    @Override
    public String createCustomer(CustomerRequest customerRequest) {
        var customer = this.customerRepository.save(mapper.toEntity(customerRequest));
        return customer.getCustomerId();
    }

    @Override
    public void updateCustomer(CustomerRequest request) {
        var customer = this.customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Cannot update customer:: No customer found with the provided ID: %s", request.getId())
                ));
        mergeCustomer(customer, request);
        this.customerRepository.save(customer);
    }

    private void mergeCustomer(Customer customer, CustomerRequest request) {
        if (StringUtils.isNotBlank(request.firstname())) {
            customer.setFirstname(request.firstname());
        }
        if (StringUtils.isNotBlank(request.email())) {
            customer.setEmail(request.email());
        }
        if (request.address() != null) {
            customer.setAddress(request.address());
        }
    }

    @Override
    public List<CustomerResponseDTO> findAllCustomers() {
        return this.customerRepository.findAll()
                .stream()
                .map(this.mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO findById(Integer customerId) {
        return this.customerRepository.findById(customerId)
                .map(mapper::toResponseDTO)
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