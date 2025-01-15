package dev.dini.account.service.transaction;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service",
        url = "${application.config.transaction-url}")
public interface TransactionFeignClient {

    @PostMapping("/transactions")
    void createTransaction(@RequestBody TransactionDTO transactionDTO);
}
