package dev.dini.customerservice.customer;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer > {
    // Method to find a customer by customerId
    Optional<Customer> findByCustomerId(Integer customerId);

    Optional<Object> findByEmail(String email);

    Optional<Object> findByPhoneNumber(String phoneNumber);

    boolean existsByIdNumber( Integer idNumber);
}
