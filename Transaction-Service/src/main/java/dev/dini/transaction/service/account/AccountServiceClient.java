package dev.dini.transaction.service.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@FeignClient(name = "account-service", url = "http://account-service-url")
public interface AccountServiceClient {

    @GetMapping("/api/accounts/{accountId}")
    AccountDTO getAccount(@PathVariable("accountId") Integer accountId);

    @PostMapping("/api/accounts/{senderAccountId}/deduct")
    void deductFunds(@PathVariable("senderAccountId") Integer senderAccountId, BigDecimal amount);

    @PostMapping("/api/accounts/{receiverAccountId}/add")
    void addFunds(@PathVariable("receiverAccountId") Integer receiverAccountId, BigDecimal amount);

    @GetMapping("/api/accounts/{accountId}")
    AccountDTO getAccountById(@PathVariable("accountId") Integer accountId);
}
