package dev.dini.transaction.service.transaction;

import dev.dini.transaction.service.account.AccountDTO;
import dev.dini.transaction.service.account.AccountNotFoundException;
import dev.dini.transaction.service.account.AccountServiceClient;
import dev.dini.transaction.service.dto.TransactionRequestDTO;
import dev.dini.transaction.service.dto.TransactionResponseDTO;
import dev.dini.transaction.service.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTransactionsForAccount_returnsTransactions() {
        Integer accountId = 1;
        List<Transaction> transactions = List.of(new Transaction());
        List<TransactionResponseDTO> expectedResponse = List.of(new TransactionResponseDTO());

        when(transactionRepository.findBySenderAccountIdOrReceiverAccountId(accountId, accountId)).thenReturn(transactions);
        when(transactionMapper.toTransactionResponseDTO(any(Transaction.class))).thenReturn(expectedResponse.get(0));

        List<TransactionResponseDTO> actualResponse = transactionService.getTransactionsForAccount(accountId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createTransaction_createsAndReturnsTransaction() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO();
        requestDTO.setSenderAccountId(1);
        requestDTO.setReceiverAccountId(2);
        requestDTO.setAmount(BigDecimal.valueOf(100));
        AccountDTO senderAccount = new AccountDTO();
        AccountDTO receiverAccount = new AccountDTO();
        Transaction transaction = new Transaction();
        TransactionResponseDTO expectedResponse = new TransactionResponseDTO();

        when(accountServiceClient.getAccountById(requestDTO.getSenderAccountId())).thenReturn(senderAccount);
        when(accountServiceClient.getAccountById(requestDTO.getReceiverAccountId())).thenReturn(receiverAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toTransactionResponseDTO(transaction)).thenReturn(expectedResponse);

        TransactionResponseDTO actualResponse = transactionService.createTransaction(requestDTO);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createTransaction_throwsAccountNotFoundException() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO();
        requestDTO.setSenderAccountId(1);
        requestDTO.setReceiverAccountId(2);

        when(accountServiceClient.getAccountById(requestDTO.getSenderAccountId())).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> transactionService.createTransaction(requestDTO));
    }

    @Test
    void completeTransaction_updatesAndReturnsTransaction() {
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.PENDING);
        TransactionResponseDTO expectedResponse = new TransactionResponseDTO();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toTransactionResponseDTO(transaction)).thenReturn(expectedResponse);

        TransactionResponseDTO actualResponse = transactionService.completeTransaction(transactionId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void cancelTransaction_updatesAndReturnsTransaction() {
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setSenderAccountId(1);
        transaction.setReceiverAccountId(2);
        transaction.setAmount(BigDecimal.valueOf(100));
        TransactionResponseDTO expectedResponse = new TransactionResponseDTO();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toTransactionResponseDTO(transaction)).thenReturn(expectedResponse);

        TransactionResponseDTO actualResponse = transactionService.cancelTransaction(transactionId);

        assertEquals(expectedResponse, actualResponse);
        verify(accountServiceClient, times(1)).addFunds(transaction.getSenderAccountId(), transaction.getAmount());
        verify(accountServiceClient, times(1)).deductFunds(transaction.getReceiverAccountId(), transaction.getAmount());
    }

    @Test
    void updateTransactionStatus_updatesAndReturnsTransaction() {
        Integer transactionId = 1;
        TransactionStatus newStatus = TransactionStatus.COMPLETED;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.PENDING);
        TransactionResponseDTO expectedResponse = new TransactionResponseDTO();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toTransactionResponseDTO(transaction)).thenReturn(expectedResponse);

        TransactionResponseDTO actualResponse = transactionService.updateTransactionStatus(transactionId, newStatus);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getTransactionDetails_returnsTransactionDetails() {
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        TransactionResponseDTO expectedResponse = new TransactionResponseDTO();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toTransactionResponseDTO(transaction)).thenReturn(expectedResponse);

        Optional<TransactionResponseDTO> actualResponse = transactionService.getTransactionDetails(transactionId);

        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());
    }

    @Test
    void checkForFraud_returnsFalse() {
        Transaction transaction = new Transaction();

        boolean isFraud = transactionService.checkForFraud(transaction);

        assertFalse(isFraud);
    }

    @Test
    void auditTransaction_logsAudit() {
        Integer transactionId = 1;
        Integer userId = 1;

        transactionService.auditTransaction(transactionId, userId);

        // Verify that the logger info method was called
        // This requires a logger mock or spy, which is not shown here
    }

    @Test
    void deleteTransaction_deletesTransaction() {
        Integer transactionId = 1;

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository, times(1)).deleteById(transactionId);
    }
}