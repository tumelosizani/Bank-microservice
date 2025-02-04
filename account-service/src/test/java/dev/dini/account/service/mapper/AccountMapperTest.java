package dev.dini.account.service.mapper;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.AccountResponseDTO;
import dev.dini.account.service.dto.CreateAccountRequestDTO;
import dev.dini.account.service.account.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountMapperTest {

    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        accountMapper = Mappers.getMapper(AccountMapper.class);
        assertNotNull(accountMapper, "Mapper could not be initialized.");
    }

    @Test
    void updateAccountFromDto_withValidData_updatesAccount() {
        // Given
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.setAccountName("Updated Name");
        accountRequestDTO.setAccountType(AccountType.CHECKING);
        accountRequestDTO.setBalance(new BigDecimal("100.00"));
        accountRequestDTO.setCustomerId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Account existingAccount = new Account();
        existingAccount.setAccountName("Old Name");
        existingAccount.setAccountType(AccountType.SAVINGS);
        existingAccount.setBalance(new BigDecimal("50.00"));
        existingAccount.setCustomerId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        // When
        accountMapper.updateAccountFromDto(accountRequestDTO, existingAccount);

        // Then
        assertEquals("Updated Name", existingAccount.getAccountName());
        assertEquals(AccountType.CHECKING, existingAccount.getAccountType());
        assertEquals(new BigDecimal("100.00"), existingAccount.getBalance());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), existingAccount.getCustomerId());
    }

    @Test
    void toAccountFromCreateRequest_withValidData_returnsAccount() {
        // Given
        CreateAccountRequestDTO createAccountRequestDTO = new CreateAccountRequestDTO();
        createAccountRequestDTO.setAccountName("New Account");
        createAccountRequestDTO.setAccountType(AccountType.SAVINGS);
        createAccountRequestDTO.setCustomerId(UUID.fromString("00000000-0000-0000-0000-000000000002"));

        // When
        Account account = accountMapper.toAccountFromCreateRequest(createAccountRequestDTO);

        // Then
        assertNotNull(account, "Account should not be null");
        assertEquals("New Account", account.getAccountName());
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), account.getCustomerId());
    }

    @Test
    void toAccountResponseDTOAfterUpdate_withValidData_returnsAccountResponseDTO() {
        // Given
        UUID accountId = UUID.randomUUID();
        Account updatedAccount = new Account();
        updatedAccount.setAccountName("Updated Account");
        updatedAccount.setAccountType(AccountType.CHECKING);
        updatedAccount.setBalance(new BigDecimal("200.00"));
        updatedAccount.setCustomerId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        updatedAccount.setAccountId(accountId);
        updatedAccount.setCreatedAt(LocalDateTime.of(2025, 1, 1, 0, 0));
        updatedAccount.setUpdatedAt(LocalDateTime.of(2025, 1, 15, 0, 0));

        // When
        AccountResponseDTO accountResponseDTO = accountMapper.toAccountResponseDTOAfterUpdate(updatedAccount);

        // Then
        assertNotNull(accountResponseDTO, "AccountResponseDTO should not be null");
        assertEquals("Updated Account", accountResponseDTO.getAccountName());
        assertEquals(AccountType.CHECKING, accountResponseDTO.getAccountType());
        assertEquals(new BigDecimal("200.00"), accountResponseDTO.getBalance());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), accountResponseDTO.getCustomerId());
        assertEquals(4, accountResponseDTO.getAccountId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), accountResponseDTO.getCreatedAt());
        assertEquals(LocalDateTime.of(2025, 1, 15, 0, 0), accountResponseDTO.getUpdatedAt());
    }
}