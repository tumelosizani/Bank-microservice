package dev.dini.transaction.service.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", url = "${application.config.customer-url}")
public interface CustomerServiceClient {

    @GetMapping("/api/v1/customers/{customerId}")
    CustomerDTO getCustomer(@PathVariable("customerId") Integer customerId);
}