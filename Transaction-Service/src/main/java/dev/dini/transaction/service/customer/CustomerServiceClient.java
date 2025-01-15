package dev.dini.transaction.service.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", url = "http://customer-service-url")
public interface CustomerServiceClient {

    @GetMapping("/api/customers/{customerId}")
    CustomerDTO getCustomer(@PathVariable("customerId") Integer customerId);


}