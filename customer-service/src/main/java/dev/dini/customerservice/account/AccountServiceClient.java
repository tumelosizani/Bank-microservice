package dev.dini.customerservice.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "account-service", url = "${application.config.account-url}")
public interface AccountServiceClient {

    // Retrieve a single account by ID
    @GetMapping("/api/v1/accounts/{accountId}")
    AccountDTO getAccountById(@PathVariable("accountId") Integer accountId);

    // Retrieve all accounts linked to a specific customer
    @GetMapping("/api/v1/accounts/customer/{customerId}")
    List<AccountDTO> getAccountsByCustomerId(@PathVariable("customerId") Integer customerId);

    // Deduct funds from an account
    @PostMapping("/api/v1/accounts/deductFunds")
    void deductFunds(@RequestParam("senderAccountId") Integer senderAccountId,
                     @RequestParam("amount") BigDecimal amount);

    // Add funds to an account
    @PostMapping("/api/v1/accounts/addFunds")
    void addFunds(@RequestParam("receiverAccountId") Integer receiverAccountId,
                  @RequestParam("amount") BigDecimal amount);

    // Link an account to a customer
    @PostMapping("/api/v1/accounts/{accountId}/linkCustomer")
    void linkAccountToCustomer(@PathVariable("accountId") Integer accountId,
                               @RequestParam("customerId") Integer customerId);
}