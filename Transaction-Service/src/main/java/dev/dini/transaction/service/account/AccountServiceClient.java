package dev.dini.transaction.service.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "account-service", url = "http://account-service-url")
public interface AccountServiceClient {

    @GetMapping("/api/accounts/{accountId}")
    AccountDTO getAccountId(@PathVariable("accountId") Integer accountId);

    @PutMapping("/api/accounts/{senderAccountId}/deduct")
    void deductFunds(@PathVariable("senderAccountId") Integer senderAccountId, @RequestParam("amount") BigDecimal amount);

    @PutMapping("/api/accounts/{receiverAccountId}/add")
    void addFunds(@PathVariable("receiverAccountId") Integer receiverAccountId, @RequestParam("amount") BigDecimal amount);

    @GetMapping("/api/accounts/{senderAccountId}")
    AccountDTO getAccountById(@PathVariable("senderAccountId") Integer senderAccountId);
}