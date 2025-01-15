package dev.dini.customerservice.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "account-service", url = "http://account-service-url")
public interface AccountServiceClient {

    @GetMapping("/accounts/{accountId}")
    AccountDTO getAccount(@PathVariable("accountId") Integer accountId);

    @PostMapping("/accounts/deductFunds")
    void deductFunds(@RequestParam("senderAccountId") Integer senderAccountId, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/accounts/addFunds")
    void addFunds(@RequestParam("receiverAccountId") Integer receiverAccountId, @RequestParam("amount") BigDecimal amount);

    @GetMapping("/accounts/{accountId}")
    AccountDTO getAccountById(@PathVariable("accountId") Integer accountId);
}