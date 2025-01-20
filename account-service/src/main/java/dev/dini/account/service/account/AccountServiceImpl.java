package dev.dini.account.service.account;

import dev.dini.account.service.customer.CustomerDTO;
import dev.dini.account.service.customer.CustomerServiceClient;
import dev.dini.account.service.dto.AccountRequestDTO;
import dev.dini.account.service.dto.AccountResponseDTO;
import dev.dini.account.service.dto.CreateAccountRequestDTO;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.exception.InsufficientFundsException;
import dev.dini.account.service.mapper.AccountMapper;
import dev.dini.account.service.transaction.TransactionDTO;
import dev.dini.account.service.transaction.TransactionFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionFeignClient transactionFeignClient;
    private final CustomerServiceClient customerServiceClient;

    @Override
    public Account createAccount(CreateAccountRequestDTO createAccountRequestDTO) {
        CustomerDTO customerDTO = customerServiceClient.getCustomerById(createAccountRequestDTO.getCustomerId());

        Account newAccount = accountMapper.toAccountFromCreateRequest(createAccountRequestDTO);
        newAccount.setCustomerId(customerDTO.getCustomerId());
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
    public AccountResponseDTO updateAccount(Integer accountId, AccountRequestDTO accountRequestDTO) {
        Account existingAccount = getAccount(accountId);
        accountMapper.updateAccountFromDto(accountRequestDTO, existingAccount);
        existingAccount.setUpdatedAt(LocalDateTime.now());
        Account updatedAccount = accountRepository.save(existingAccount);
        return accountMapper.toAccountResponseDTOAfterUpdate(updatedAccount);
    }

    @Override
    public void transferFunds(Integer fromAccountId, Integer toAccountId, BigDecimal amount) {
        Account fromAccount = getAccount(fromAccountId);
        Account toAccount = getAccount(toAccountId);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + fromAccountId);
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        processTransaction(fromAccountId, toAccountId, amount);
    }

    @Override
    public Account changeAccountType(Integer accountId, AccountType newAccountType) {
        Account account = getAccount(accountId);
        account.setAccountType(newAccountType);
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    @Override
    public void setOverdraftProtection(Integer accountId, boolean enabled) {
        Account account = getAccount(accountId);
        account.setOverdraftProtection(enabled);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Override
    public void freezeAccount(Integer accountId) {
        Account account = getAccount(accountId);
        account.setStatus(AccountStatus.FROZEN);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Override
    public void unfreezeAccount(Integer accountId) {
        Account account = getAccount(accountId);
        account.setStatus(AccountStatus.ACTIVE);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Override
    public void addAccountHolder(Integer accountId, Integer customerId) {
        Account account = getAccount(accountId);
        account.addAccountHolder(customerId); // Assuming there's a method to add account holders
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Override
    public void removeAccountHolder(Integer accountId, Integer customerId) {
        Account account = getAccount(accountId);
        account.removeAccountHolder(customerId); // Assuming there's a method to remove account holders
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Override
    public AccountStatus checkAccountStatus(Integer accountId) {
        Account account = getAccount(accountId);
        return account.getStatus();
    }

    @Override
    public void setTransactionLimit(Integer accountId, BigDecimal limit) {
        Account account = getAccount(accountId);
        account.setTransactionLimit(limit);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    public void processTransaction(Integer fromAccountId, Integer toAccountId, BigDecimal amount) {

        if (fromAccountId == null || toAccountId == null || amount == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }
        // Create a transaction DTO
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setFromAccountId(fromAccountId);
        transactionDTO.setToAccountId(toAccountId);
        transactionDTO.setAmount(amount);

        // Call the Transaction service using Feign Client
        transactionFeignClient.createTransaction(transactionDTO);
    }

    public void linkAccountToCustomer(Integer accountId, Integer customerId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No account found with ID: " + accountId));
        account.setCustomerId(customerId); // Update the customer ID
        accountRepository.save(account);
    }

    @Override
    public List<Account> getAccountsByCustomerId(Integer customerId) {
        CustomerDTO customerDTO = customerServiceClient.getCustomerById(customerId);

        if (customerDTO == null){
            throw new AccountNotFoundException("No customer found with ID: " + customerId);
        }
        return accountRepository.findByCustomerId(customerId);
    }
}