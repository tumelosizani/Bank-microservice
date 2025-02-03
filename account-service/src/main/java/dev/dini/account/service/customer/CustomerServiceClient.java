package dev.dini.account.service.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-service", url = "${application.config.customer-url}")
public interface CustomerServiceClient {

    @GetMapping("/api/v1/customers/{customerId}")
    CustomerDTO getCustomerById(@PathVariable("customerId") UUID customerId);
}