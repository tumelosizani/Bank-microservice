package dev.dini.account.service.transaction;

import dev.dini.account.service.account.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class TransactionFeignClientTest {

    @Mock
    private TransactionFeignClient transactionFeignClient;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransaction_withValidData_callsCreateTransaction() {
        doNothing().when(transactionFeignClient).createTransaction(new TransactionDTO());

        accountService.processTransaction(1, 2, BigDecimal.TEN);

        ArgumentCaptor<TransactionDTO> captor = ArgumentCaptor.forClass(TransactionDTO.class);
        verify(transactionFeignClient).createTransaction(captor.capture());
        TransactionDTO capturedTransaction = captor.getValue();

        assertEquals(1, capturedTransaction.getFromAccountId());
        assertEquals(2, capturedTransaction.getToAccountId());
        assertEquals(BigDecimal.TEN, capturedTransaction.getAmount());
    }

    @Test
    void createTransaction_withNullData_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.processTransaction(null, 2, BigDecimal.TEN);
        });
    }
}