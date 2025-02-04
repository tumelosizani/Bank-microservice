package dev.dini.account.service.mapper;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.AccountResponseDTO;
import dev.dini.account.service.dto.CreateAccountRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    // Update Account entity from AccountRequestDTO
    void updateAccountFromDto(AccountRequestDTO accountRequestDTO, @MappingTarget Account existingAccount);

    // Map CreateAccountRequestDTO to Account entity (if needed for account creation)
    Account toAccountFromCreateRequest(CreateAccountRequestDTO createAccountRequestDTO);

    // Map Account entity to AccountResponseDTO after update
    AccountResponseDTO toAccountResponseDTOAfterUpdate(Account updatedAccount);

    AccountResponseDTO toAccountResponseDTO(Account account);
}
