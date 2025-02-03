package dev.dini.transaction.service.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "account-service", url = "${application.config.account-url}")
public interface AccountServiceClient {

    @GetMapping("/api/v1/accounts/{accountId}")
    AccountDTO getAccountById(@PathVariable("accountId") Integer accountId);

    @PutMapping("/api/v1/accounts/{senderAccountId}/deduct")
    void deductFunds(@PathVariable("senderAccountId") Integer senderAccountId,
                     @RequestParam("amount") BigDecimal amount);

    @PutMapping("/api/v1/accounts/{receiverAccountId}/add")
    void addFunds(@PathVariable("receiverAccountId") Integer receiverAccountId,
                  @RequestParam("amount") BigDecimal amount);
}