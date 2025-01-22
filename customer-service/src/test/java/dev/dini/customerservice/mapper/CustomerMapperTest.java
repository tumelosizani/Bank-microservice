package dev.dini.customerservice.mapper;

import dev.dini.customerservice.customer.Customer;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerMapperTest {

    private CustomerMapper customerMapper;

    @BeforeEach
    void setUp() {
        customerMapper = Mappers.getMapper(CustomerMapper.class);
    }

    @Test
    void toResponseDTO_mapsCustomerToCustomerResponseDTO() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setFirstname("John");
        customer.setLastname("Doe");

        CustomerResponseDTO responseDTO = customerMapper.toResponseDTO(customer);

        assertEquals(customer.getCustomerId(), responseDTO.getCustomerId());
        assertEquals(customer.getFirstname(), responseDTO.getFirstname());
        assertEquals(customer.getLastname(), responseDTO.getLastname());
    }

    @Test
    void toEntity_mapsCreateCustomerDTOToCustomer() {
        CreateCustomerDTO createCustomerDTO = new CreateCustomerDTO();
        createCustomerDTO.setFirstname("Jane");
        createCustomerDTO.setLastname("Smith");

        Customer customer = customerMapper.toEntity(createCustomerDTO);

        assertEquals(createCustomerDTO.getFirstname(), customer.getFirstname());
        assertEquals(createCustomerDTO.getLastname(), customer.getLastname());
    }

    @Test
    void toUpdateCustomerDTO_mapsCustomerToUpdateCustomerDTO() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setFirstname("John");
        customer.setLastname("Doe");

        UpdateCustomerDTO updateCustomerDTO = customerMapper.toUpdateCustomerDTO(customer);

        assertEquals(updateCustomerDTO.getFirstname(), customer.getFirstname());
        assertEquals(updateCustomerDTO.getLastname(), customer.getLastname());
    }

    @Test
    void updateCustomerFromDto_updatesCustomerWithUpdateCustomerDTO() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setFirstname("John");
        customer.setLastname("Doe");

        UpdateCustomerDTO updateCustomerDTO = new UpdateCustomerDTO();
        updateCustomerDTO.setFirstname("Jane");
        updateCustomerDTO.setLastname("Doe");

        customerMapper.updateCustomerFromDto(updateCustomerDTO, customer);

        assertEquals(updateCustomerDTO.getFirstname(), customer.getFirstname());
        assertEquals(updateCustomerDTO.getLastname(), customer.getLastname());
    }
}