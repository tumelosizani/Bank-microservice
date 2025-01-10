package dev.dini.account.service.account;

import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.CreateAccountRequestDTO;
import dev.dini.account.service.dto.TransactionDTO;
import dev.dini.account.service.mapper.AccountMapper;
import dev.dini.account.service.transaction.TransactionFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionFeignClient transactionFeignClient;

    @Override
    public Account createAccount(CreateAccountRequestDTO createAccountRequestDTO) {
        Account newAccount = new Account();
        newAccount.setAccountId(UUID.randomUUID().hashCode());
        newAccount.setUserId(createAccountRequestDTO.getUserId());
        newAccount.setAccountType(createAccountRequestDTO.getAccountType());
        newAccount.setBalance(BigDecimal.ZERO); // Initial balance is zero
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(newAccount);
    }

    @Override
    public Account getAccount(Integer accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public void closeAccount(Integer accountId) {
        Account existingAccount = getAccount(accountId);
        accountRepository.delete(existingAccount);
    }

    @Override
    public BigDecimal getBalance(Integer accountId) {
        Account account = getAccount(accountId);
        return account.getBalance();
    }

    @Override
    public Account updateAccount(Integer accountId, AccountRequestDTO accountRequestDTO) {
        Account existingAccount = getAccount(accountId);
        accountMapper.updateAccountFromDto(accountRequestDTO, existingAccount);
        existingAccount.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(existingAccount);
    }

    public void processTransaction(Integer fromAccountId,Integer toAccountId, BigDecimal amount) {
        // Create a transaction DTO
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setFromAccountId(fromAccountId);
        transactionDTO.setToAccountId(toAccountId);
        transactionDTO.setAmount(amount);

        // Call the Transaction service using Feign Client
        transactionFeignClient.createTransaction(transactionDTO);
    }
}