package dev.dini.account.service.interest;

import dev.dini.account.service.account.Account;
import dev.dini.account.service.account.AccountRepository;
import dev.dini.account.service.account.AccountType;
import dev.dini.account.service.dto.InterestCalculationRequestDTO;
import dev.dini.account.service.dto.InterestCalculationResponseDTO;
import dev.dini.account.service.exception.AccountNotFoundException;
import dev.dini.account.service.notification.AccountNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InterestCalculationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNotificationService accountNotificationService;

    @Mock
    private InterestHistoryRepository interestHistoryRepository;

    @InjectMocks
    private InterestCalculationService interestCalculationService;

    private UUID accountId;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();
        account = new Account();
        account.setAccountId(accountId);
        account.setBalance(BigDecimal.valueOf(1000));
        account.setAccountType(AccountType.SAVINGS);
    }

    @Test
    void calculateInterestSuccessfully() {
        InterestCalculationRequestDTO request = new InterestCalculationRequestDTO(accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        InterestCalculationResponseDTO response = interestCalculationService.calculateInterest(request);

        verify(accountRepository).save(account);
        assertEquals(0, BigDecimal.valueOf(30.00).compareTo(response.getInterestAmount()));
    }

    @Test
    void calculateInterestThrowsAccountNotFoundException() {
        InterestCalculationRequestDTO request = new InterestCalculationRequestDTO(accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> interestCalculationService.calculateInterest(request));
    }

    @Test
    void applyInterestSuccessfully() {
        when(accountRepository.findByAccountType(AccountType.SAVINGS)).thenReturn(List.of(account));

        interestCalculationService.applyInterest();

        verify(accountRepository).save(account);
        verify(accountNotificationService).sendInterestAppliedNotification(eq(account), any(BigDecimal.class));
    }

    @Test
    void logInterestHistorySuccessfully() {
        BigDecimal interestAmount = BigDecimal.valueOf(30.00);

        interestCalculationService.logInterestHistory(account, interestAmount);

        verify(interestHistoryRepository).save(any(InterestHistory.class));
    }

    @Test
    void previewInterestApplicationSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        BigDecimal previewInterest = interestCalculationService.previewInterestApplication(account);

        assertEquals(0, BigDecimal.valueOf(2.50).compareTo(previewInterest));
    }
}