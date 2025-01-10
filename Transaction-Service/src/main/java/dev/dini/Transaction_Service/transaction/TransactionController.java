package dev.dini.Transaction_Service.transaction;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;


@RestController
@RequestMapping("/transactions")
@Validated // To support validation annotations in request bodies
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody @Valid CreateTransactionRequest request) {
        try {
            // Create a new transaction
            Transaction transaction = transactionService.createTransaction(
                    request.getAccountId(),
                    request.getAmount(),
                    request.getTransactionType(),
                    request.getDescription()
            );
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Bad request if input is invalid (e.g., amount <= 0)
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Integer transactionId) {
        // Retrieve a transaction by its ID
        Optional<Transaction> transaction = transactionService.getTransaction(transactionId);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{transactionId}/complete")
    public ResponseEntity<Transaction> completeTransaction(@PathVariable Integer transactionId) {
        try {
            // Mark the transaction as completed
            Transaction transaction = transactionService.completeTransaction(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{transactionId}/cancel")
    public ResponseEntity<Transaction> cancelTransaction(@PathVariable Integer transactionId) {
        try {
            // Mark the transaction as cancelled
            Transaction transaction = transactionService.cancelTransaction(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer transactionId) {
        try {
            // Delete the transaction
            transactionService.deleteTransaction(transactionId);
            return ResponseEntity.noContent().build();
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
