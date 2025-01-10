package dev.dini.customerservice.customer;

import java.util.List;

public interface CustomerService {
    String createCustomer(CustomerRequest request);

    void updateCustomer(CustomerRequest request);

    List<CustomerResponse> findAllCustomers();

    CustomerResponse findById(String id);

    boolean existsById(String id);

    void deleteCustomer(String id);
}
