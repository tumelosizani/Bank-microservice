package dev.dini.account.service.mapper;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.AccountResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    // Map AccountRequestDTO to Account entity
    Account toAccount(AccountRequestDTO accountRequestDTO);

    // Map Account entity to AccountResponseDTO
    AccountResponseDTO toAccountResponseDTO(Account account);

    // Update Account entity from AccountRequestDTO
    void updateAccountFromDto(AccountRequestDTO accountRequestDTO, @MappingTarget Account existingAccount);
}