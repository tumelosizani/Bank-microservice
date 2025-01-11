package dev.dini.customerservice.mapper;

import dev.dini.customerservice.customer.Customer;
import dev.dini.customerservice.dto.CustomerResponseDTO;
import dev.dini.customerservice.dto.CreateCustomerDTO;
import dev.dini.customerservice.dto.UpdateCustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // Map Customer to CustomerResponseDTO
    CustomerResponseDTO toResponseDTO(Customer customer);

    // Map CreateCustomerDTO to Customer
    Customer toEntity(CreateCustomerDTO createCustomerDTO);

    // Map Customer to UpdateCustomerDTO (Optional, if needed)
    UpdateCustomerDTO toUpdateCustomerDTO(Customer customer);

    // Method to update the existing customer with the provided DTO
    void updateCustomerFromDto(UpdateCustomerDTO updateCustomerDTO, @MappingTarget Customer customer);
}
