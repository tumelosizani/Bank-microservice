package dev.dini.transaction.service.transaction;

import dev.dini.transaction.service.account.AccountServiceClient;
import dev.dini.transaction.service.dto.TransactionRequestDTO;
import dev.dini.transaction.service.dto.TransactionResponseDTO;
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
    private final AccountServiceClient accountServiceClient;

    public TransactionController(TransactionService transactionService, AccountServiceClient accountServiceClient) {
        this.transactionService = transactionService;
        this.accountServiceClient = accountServiceClient;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody @Valid TransactionRequestDTO request) {
        try {
            // Create a new transaction
            TransactionResponseDTO transactionResponse = transactionService.createTransaction(request);
            return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Bad request if input is invalid (e.g., amount <= 0)
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(@PathVariable Integer transactionId) {
        // Retrieve a transaction by its ID
        Optional<TransactionResponseDTO> transactionResponse = transactionService.getTransactionDetails(transactionId);
        return transactionResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{transactionId}/complete")
    public ResponseEntity<TransactionResponseDTO> completeTransaction(@PathVariable Integer transactionId) {
        try {
            // Mark the transaction as completed
            TransactionResponseDTO transactionResponse = transactionService.completeTransaction(transactionId);
            return ResponseEntity.ok(transactionResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{transactionId}/cancel")
    public ResponseEntity<TransactionResponseDTO> cancelTransaction(@PathVariable Integer transactionId) {
        try {
            // Mark the transaction as cancelled
            TransactionResponseDTO transactionResponse = transactionService.cancelTransaction(transactionId);
            return ResponseEntity.ok(transactionResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer transactionId) {
        try {
            // Delete the transaction
            transactionService.deleteTransaction(transactionId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}