package dev.dini.customerservice.customer;

import dev.dini.customerservice.dto.CustomerResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    // Map CustomerResponseDTO to Customer
    CustomerResponseDTO toResponseDTO(Customer customer);

    Customer toEntity(CustomerRequest request);


}
