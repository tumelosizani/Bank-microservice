package dev.dini.customerservice.customer;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID > {
    // Method to find a customer by customerId
    Optional<Customer> findByCustomerId(UUID customerId);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    boolean existsByIdNumber( Integer idNumber);

    // Method to find customers by first name
    List<Customer> findByFirstname(String firstname);

    // Method to find customers by last name
    List<Customer> findByLastname(String lastname);

    // Method to find customers by KYC status
    List<Customer> findByKycStatus(KycStatus kycStatus);

    // Method to find customers by creation date within a range
    List<Customer> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Method to check if a customer exists by email
    boolean existsByEmail(String email);

    List<Customer> findByFirstnameAndLastname(@NotNull String firstname, @NotNull String lastname);

}
