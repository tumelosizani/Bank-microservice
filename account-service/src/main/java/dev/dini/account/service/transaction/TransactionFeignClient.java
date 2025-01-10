package dev.dini.account.service.transaction;

import dev.dini.account.service.dto.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service")
public interface TransactionFeignClient {

    @PostMapping("/transactions")
    void createTransaction(@RequestBody TransactionDTO transactionDTO);
}
