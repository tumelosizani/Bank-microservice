package dev.dini.transaction.service.mapper;

import dev.dini.transaction.service.dto.TransactionRequestDTO;
import dev.dini.transaction.service.dto.TransactionResponseDTO;
import dev.dini.transaction.service.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionMapper = Mappers.getMapper(TransactionMapper.class);
        assertNotNull(transactionMapper, "Mapper could not be initialized.");
    }


    @Test
    void toTransaction_convertsTransactionRequestDTOToTransaction() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO();
        requestDTO.setSenderAccountId(1);
        requestDTO.setReceiverAccountId(2);
        requestDTO.setAmount(BigDecimal.valueOf(100));

        Transaction transaction = transactionMapper.toTransaction(requestDTO);

        assertEquals(requestDTO.getSenderAccountId(), transaction.getSenderAccountId());
        assertEquals(requestDTO.getReceiverAccountId(), transaction.getReceiverAccountId());
        assertEquals(requestDTO.getAmount(), transaction.getAmount());
    }

    @Test
    void toTransactionResponseDTO_convertsTransactionToTransactionResponseDTO() {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(LocalDateTime.parse("2025-01-16T15:49:49.519271"));
        transaction.setSenderAccountId(1);
        transaction.setReceiverAccountId(2);
        transaction.setAmount(BigDecimal.valueOf(100));

        TransactionResponseDTO responseDTO = transactionMapper.toTransactionResponseDTO(transaction);

        assertEquals(transaction.getSenderAccountId(), responseDTO.getSenderAccountId());
        assertEquals(transaction.getReceiverAccountId(), responseDTO.getReceiverAccountId());
        assertEquals(transaction.getAmount(), responseDTO.getAmount());
        assertEquals(transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), responseDTO.getTimestamp());
    }

    @Test
    void toTransactionResponseDTOList_convertsListOfTransactionsToListOfTransactionResponseDTO() {
        Transaction transaction1 = new Transaction();
        transaction1.setTimestamp(LocalDateTime.now());
        transaction1.setSenderAccountId(1);
        transaction1.setReceiverAccountId(2);
        transaction1.setAmount(BigDecimal.valueOf(100));

        Transaction transaction2 = new Transaction();
        transaction2.setTimestamp(LocalDateTime.now());
        transaction2.setSenderAccountId(3);
        transaction2.setReceiverAccountId(4);
        transaction2.setAmount(BigDecimal.valueOf(200));

        List<Transaction> transactions = List.of(transaction1, transaction2);

        List<TransactionResponseDTO> responseDTOs = transactionMapper.toTransactionResponseDTOList(transactions);

        assertEquals(2, responseDTOs.size());
        assertEquals(transaction1.getSenderAccountId(), responseDTOs.get(0).getSenderAccountId());
        assertEquals(transaction2.getSenderAccountId(), responseDTOs.get(1).getSenderAccountId());
    }
}
