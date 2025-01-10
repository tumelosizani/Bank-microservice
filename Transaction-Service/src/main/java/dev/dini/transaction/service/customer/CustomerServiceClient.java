package dev.dini.transaction.service.customer;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "customer-service",
        url = "http://localhost:8091")
public interface CustomerServiceClient {

}
