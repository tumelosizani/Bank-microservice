package dev.dini.customerservice.customer;

import dev.dini.customerservice.account.AccountServiceClient;
import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import dev.dini.customerservice.exception.CustomerNotFoundException;
import dev.dini.customerservice.mapper.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerServiceImplTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer_createsAndReturnsCustomerId() {
        CreateCustomerDTO createCustomerDTO = new CreateCustomerDTO();
        createCustomerDTO.setAccountIds(Collections.singletonList(1));

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCreatedAt(LocalDateTime.now());

        when(customerMapper.toEntity(any(CreateCustomerDTO.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Integer customerId = customerService.createCustomer(createCustomerDTO);

        assertNotNull(customerId);
        assertEquals(1, customerId);
        verify(accountServiceClient, times(1)).linkAccountToCustomer(1, 1);
    }

    @Test
    void updateCustomer_updatesExistingCustomer() {
        UpdateCustomerDTO updateCustomerDTO = new UpdateCustomerDTO();
        updateCustomerDTO.setCustomerId(1);

        Customer customer = new Customer();
        customer.setCustomerId(1);

        when(customerRepository.findByCustomerId(1)).thenReturn(Optional.of(customer));

        customerService.updateCustomer(updateCustomerDTO);

        verify(customerMapper, times(1)).updateCustomerFromDto(updateCustomerDTO, customer);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void updateCustomer_throwsExceptionWhenCustomerNotFound() {
        UpdateCustomerDTO updateCustomerDTO = new UpdateCustomerDTO();
        updateCustomerDTO.setCustomerId(1);

        when(customerRepository.findByCustomerId(1)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(updateCustomerDTO));
    }

    @Test
    void findAllCustomers_returnsListOfCustomerResponseDTOs() {
        Customer customer = new Customer();
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();

        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));
        when(customerMapper.toResponseDTO(customer)).thenReturn(customerResponseDTO);

        List<CustomerResponseDTO> customers = customerService.findAllCustomers();

        assertNotNull(customers);
        assertEquals(1, customers.size());
        assertEquals(customerResponseDTO, customers.get(0));
    }

    @Test
    void findById_returnsCustomerResponseDTO() {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponseDTO(customer)).thenReturn(customerResponseDTO);

        CustomerResponseDTO response = customerService.findById(1);

        assertNotNull(response);
        assertEquals(customerResponseDTO, response);
    }

    @Test
    void findById_throwsExceptionWhenCustomerNotFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findById(1));
    }

    @Test
    void deleteCustomer_deletesCustomer() {
        customerService.deleteCustomer(1);

        verify(customerRepository, times(1)).deleteById(1);
    }

    @Test
    void deactivateCustomer_deactivatesCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId(1);

        when(customerRepository.findByCustomerId(1)).thenReturn(Optional.of(customer));

        customerService.deactivateCustomer(1);

        assertEquals(CustomerStatus.DEACTIVATED, customer.getStatus());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void deactivateCustomer_throwsExceptionWhenCustomerNotFound() {
        when(customerRepository.findByCustomerId(1)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.deactivateCustomer(1));
    }

    @Test
    void activateCustomer_activatesCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId(1);

        when(customerRepository.findByCustomerId(1)).thenReturn(Optional.of(customer));

        customerService.activateCustomer(1);

        assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void activateCustomer_throwsExceptionWhenCustomerNotFound() {
        when(customerRepository.findByCustomerId(1)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.activateCustomer(1));
    }

    @Test
    void suspendCustomer_suspendsCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId(1);

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        customerService.suspendCustomer(1);

        assertEquals(CustomerStatus.SUSPENDED, customer.getStatus());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void suspendCustomer_throwsExceptionWhenCustomerNotFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.suspendCustomer(1));
    }

    @Test
    void existsByCustomerId_returnsTrueWhenCustomerExists() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(new Customer()));

        assertTrue(customerService.existsByCustomerId(1));
    }

    @Test
    void existsByCustomerId_returnsFalseWhenCustomerDoesNotExist() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertFalse(customerService.existsByCustomerId(1));
    }

    @Test
    void existsByEmail_returnsTrueWhenCustomerExists() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Customer()));

        assertTrue(customerService.existsByEmail("test@example.com"));
    }

    @Test
    void existsByEmail_returnsFalseWhenCustomerDoesNotExist() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertFalse(customerService.existsByEmail("test@example.com"));
    }

    @Test
    void existsByPhoneNumber_returnsTrueWhenCustomerExists() {
        when(customerRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(new Customer()));

        assertTrue(customerService.existsByPhoneNumber("1234567890"));
    }

    @Test
    void existsByPhoneNumber_returnsFalseWhenCustomerDoesNotExist() {
        when(customerRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.empty());

        assertFalse(customerService.existsByPhoneNumber("1234567890"));
    }

    @Test
    void findByEmail_returnsCustomerResponseDTO() {
        Customer customer = new Customer();
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();

        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(customerMapper.toResponseDTO(customer)).thenReturn(customerResponseDTO);

        CustomerResponseDTO response = customerService.findByEmail("test@example.com");

        assertNotNull(response);
        assertEquals(customerResponseDTO, response);
    }

    @Test
    void findByEmail_throwsExceptionWhenCustomerNotFound() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findByEmail("test@example.com"));
    }

    @Test
    void findByPhoneNumber_returnsCustomerResponseDTO() {
        Customer customer = new Customer();
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();

        when(customerRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(customer));
        when(customerMapper.toResponseDTO(customer)).thenReturn(customerResponseDTO);

        CustomerResponseDTO response = customerService.findByPhoneNumber("1234567890");

        assertNotNull(response);
        assertEquals(customerResponseDTO, response);
    }

    @Test
    void findByPhoneNumber_throwsExceptionWhenCustomerNotFound() {
        when(customerRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findByPhoneNumber("1234567890"));
    }
}