package dev.dini.transaction.service.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "account-service",
        url = "http://account-service-url")
public interface AccountServiceClient {

    @GetMapping("/api/accounts/{accountId}")
    AccountDTO getAccount(@PathVariable("accountId") Integer accountId);

    void deductFunds(Integer senderAccountId, BigDecimal amount);

    void addFunds(Integer receiverAccountId, BigDecimal amount);

    AccountDTO getAccountById(Integer senderAccountId);

}
