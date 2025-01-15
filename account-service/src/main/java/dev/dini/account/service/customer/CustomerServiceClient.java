package dev.dini.account.service.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service",
        url = "${application.config.customer-url}")
public interface CustomerServiceClient {

    @GetMapping("/customers")
    CustomerDTO getCustomerById(@PathVariable("customerId") Integer customerId);
}
