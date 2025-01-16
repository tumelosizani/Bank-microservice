package dev.dini.transaction.service.transaction;

import dev.dini.transaction.service.account.AccountServiceClient;
import dev.dini.transaction.service.customer.CustomerServiceClient;
import dev.dini.transaction.service.dto.TransactionRequestDTO;
import dev.dini.transaction.service.dto.TransactionResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void createTransaction_returnsBadRequestForInvalidAmount() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO();
        requestDTO.setSenderAccountId(1);
        requestDTO.setReceiverAccountId(2);
        requestDTO.setAmount(BigDecimal.valueOf(-100));

        ResponseEntity<TransactionResponseDTO> response = transactionController.createTransaction(requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getTransaction_returnsNotFoundForNonExistentTransaction() {
        Integer transactionId = 999;

        when(transactionService.getTransactionDetails(transactionId)).thenReturn(Optional.empty());

        ResponseEntity<TransactionResponseDTO> response = transactionController.getTransaction(transactionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void completeTransaction_returnsNotFoundForInvalidTransactionId() {
        Integer transactionId = 999;

        when(transactionService.completeTransaction(transactionId)).thenThrow(new IllegalArgumentException());

        ResponseEntity<TransactionResponseDTO> response = transactionController.completeTransaction(transactionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void cancelTransaction_returnsNotFoundForInvalidTransactionId() {
        Integer transactionId = 999;

        when(transactionService.cancelTransaction(transactionId)).thenThrow(new IllegalArgumentException());

        ResponseEntity<TransactionResponseDTO> response = transactionController.cancelTransaction(transactionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteTransaction_returnsNotFoundForInvalidTransactionId() {
        Integer transactionId = 999;

        doThrow(new IllegalArgumentException()).when(transactionService).deleteTransaction(transactionId);

        ResponseEntity<Void> response = transactionController.deleteTransaction(transactionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateTransactionStatus_returnsBadRequestForInvalidStatus() {
        Integer transactionId = 1;
        TransactionStatus invalidStatus = null;

        ResponseEntity<TransactionResponseDTO> response = transactionController.updateTransactionStatus(transactionId, invalidStatus);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}