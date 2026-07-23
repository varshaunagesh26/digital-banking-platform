package com.db.transaction;

import com.db.transaction.client.AccountServiceClient;
import com.db.transaction.entity.TransactionEntity;
import com.db.transaction.mapper.CycleAvoidMappingContext;
import com.db.transaction.mapper.TransactionMapper;
import com.db.transaction.repository.TransactionRepository;
import com.db.transaction.service.TransactionService;
import com.digital.backend.events.TransactionEvent;
import com.digital.backend.exceptions.EntityNotFoundException;
import com.digital.backend.model.Transaction;
import com.digital.backend.model.TransactionInput;
import com.digital.backend.model.enums.Currency;
import com.digital.backend.model.enums.PaymentType;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import com.digital.backend.model.paymentservice.PaymentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TransactionService}.
 *
 * NOTE: {@code PaymentEvent} source was not supplied in this thread, so it is
 * constructed the same way {@code PaymentService.mapToEvent} constructs it
 * elsewhere in this codebase: {@code new PaymentEvent()} + setters. Adjust if the
 * real class instead exposes a builder.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountServiceClient accountServiceClient; // unused by service logic, but required for @InjectMocks

    @InjectMocks
    private TransactionService transactionService;

    private static final String TXN_NUMBER = "txn-abc-123";
    private static final Long FROM_ACCOUNT = 1111000011110000L;
    private static final Long TO_ACCOUNT = 2222000022220000L;
    private static final String FROM_IFSC = "SBIN0001234";
    private static final String TO_IFSC = "HDFC0005678";

    private TransactionEntity sampleEntity;

    @BeforeEach
    void setUp() {
        // Arrange (shared): a baseline persisted-looking entity reused across tests
        sampleEntity = TransactionEntity.builder()
                .id(1L)
                .transactionNumber(TXN_NUMBER)
                .fromAccountNumber(FROM_ACCOUNT)
                .fromIFSCCode(FROM_IFSC)
                .toAccountNumber(TO_ACCOUNT)
                .toIFSCCode(TO_IFSC)
                .amount(1500.00)
                .currency(Currency.INR)
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.OPENED)
                .createdBy("transaction-service-transfer")
                .timestamp(Instant.now())
                .build();
    }

    private Transaction sampleDto() {
        // Transaction has no builder - construct via no-arg ctor + setters
        Transaction dto = new Transaction();
        dto.setTransactionNumber(TXN_NUMBER);
        dto.setFromAccountNumber(FROM_ACCOUNT);
        dto.setFromIFSCCode(FROM_IFSC);
        dto.setToAccountNumber(TO_ACCOUNT);
        dto.setToIFSCCode(TO_IFSC);
        dto.setAmount(1500.00);
        dto.setCurrency(Currency.INR);
        dto.setType(TransactionType.TRANSFER);
        dto.setStatus(TransactionStatus.OPENED);
        return dto;
    }

    // ---------------------------------------------------------------------
    // Happy Path Tests
    // ---------------------------------------------------------------------

    @Nested
    @DisplayName("Happy Path scenarios")
    class HappyPathTests {

        @Test
        @DisplayName("Should return a transaction DTO when found by transaction number")
        void getTransactionByTransactionNumber_Found_ReturnsDto() {
            // Arrange
            Transaction expectedDto = sampleDto();
            when(transactionRepository.findTransactionByTransactionNumber(TXN_NUMBER))
                    .thenReturn(Optional.of(sampleEntity));
            when(transactionMapper.toDto(eq(sampleEntity), any(CycleAvoidMappingContext.class)))
                    .thenReturn(expectedDto);

            // Act
            Transaction result = transactionService.getTransactionByTransactionNumber(TXN_NUMBER);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(transactionRepository, times(1)).findTransactionByTransactionNumber(TXN_NUMBER);
        }

        @Test
        @DisplayName("Should return a mapped list of transactions filtered by status")
        void getTransactionByTransactionStatus_ReturnsMappedList() {
            // Arrange
            TransactionEntity second = TransactionEntity.builder()
                    .transactionNumber("txn-second")
                    .status(TransactionStatus.OPENED)
                    .build();
            when(transactionRepository.findTransactionByStatus(TransactionStatus.OPENED))
                    .thenReturn(List.of(sampleEntity, second));
            when(transactionMapper.toDto(any(TransactionEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(sampleDto());

            // Act
            List<Transaction> results = transactionService.getTransactionByTransactionStatus(TransactionStatus.OPENED);

            // Assert
            assertEquals(2, results.size());
            verify(transactionMapper, times(2))
                    .toDto(any(TransactionEntity.class), any(CycleAvoidMappingContext.class));
        }

        @Test
        @DisplayName("Should return all transactions mapped to DTOs")
        void getAllTransactions_ReturnsMappedList() {
            // Arrange
            when(transactionRepository.findAll()).thenReturn(List.of(sampleEntity));
            when(transactionMapper.toDto(any(TransactionEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(sampleDto());

            // Act
            List<Transaction> results = transactionService.getAllTransactions();

            // Assert
            assertEquals(1, results.size());
            verify(transactionRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should update transaction status and persist the change when transaction exists")
        void updateTransactionStatus_Found_UpdatesAndReturnsDto() {
            // Arrange
            when(transactionRepository.findTransactionByTransactionNumber(TXN_NUMBER))
                    .thenReturn(Optional.of(sampleEntity));
            when(transactionMapper.toDto(eq(sampleEntity), any(CycleAvoidMappingContext.class)))
                    .thenReturn(sampleDto());

            // Act
            Transaction result = transactionService.updateTransactionStatus(TXN_NUMBER, TransactionStatus.COMPLETED);

            // Assert
            assertNotNull(result);
            assertEquals(TransactionStatus.COMPLETED, sampleEntity.getStatus());
            verify(transactionRepository, times(1)).save(sampleEntity);
        }

        @Test
        @DisplayName("Should create a deposit transaction with OPENED status and correct fields")
        void depositMoney_CreatesEntityAndReturnsDto() {
            // Arrange
            TransactionInput deposit = TransactionInput.builder()
                    .toAccountNumber(TO_ACCOUNT)
                    .toIFSCCode(TO_IFSC)
                    .amount(750.00)
                    .build();
            when(transactionMapper.toDto(any(TransactionEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(sampleDto());

            // Act
            Transaction result = transactionService.depositMoney(deposit);

            // Assert
            assertNotNull(result);
            ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionRepository, times(1)).save(captor.capture());
            TransactionEntity saved = captor.getValue();
            assertEquals(TO_ACCOUNT, saved.getToAccountNumber());
            assertEquals(TO_IFSC, saved.getToIFSCCode());
            assertEquals(750.00, saved.getAmount());
            assertEquals(TransactionStatus.OPENED, saved.getStatus());
            assertEquals(Currency.INR, saved.getCurrency());
            assertEquals(TransactionType.DEPOSIT, saved.getType());
            assertEquals("transaction-service-deposit", saved.getCreatedBy());
            assertNotNull(saved.getTimestamp());
        }

        @Test
        @DisplayName("Should create a withdraw transaction with OPENED status and correct fields")
        void withdrawMoney_CreatesEntityAndReturnsDto() {
            // Arrange
            TransactionInput withdraw = TransactionInput.builder()
                    .fromAccountNumber(FROM_ACCOUNT)
                    .fromIFSCCode(FROM_IFSC)
                    .amount(300.00)
                    .build();
            when(transactionMapper.toDto(any(TransactionEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(sampleDto());

            // Act
            Transaction result = transactionService.withdrawMoney(withdraw);

            // Assert
            assertNotNull(result);
            ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionRepository, times(1)).save(captor.capture());
            TransactionEntity saved = captor.getValue();
            assertEquals(FROM_ACCOUNT, saved.getFromAccountNumber());
            assertEquals(FROM_IFSC, saved.getFromIFSCCode());
            assertEquals(300.00, saved.getAmount());
            assertEquals(TransactionStatus.OPENED, saved.getStatus());
            assertEquals(TransactionType.WITHDRAW, saved.getType());
            assertEquals("transaction-service-withdraw", saved.getCreatedBy());
        }

        @Test
        @DisplayName("Should create a transfer transaction with both account legs populated")
        void transferMoney_CreatesEntityAndReturnsDto() {
            // Arrange
            TransactionInput transfer = TransactionInput.builder()
                    .fromAccountNumber(FROM_ACCOUNT)
                    .fromIFSCCode(FROM_IFSC)
                    .toAccountNumber(TO_ACCOUNT)
                    .toIFSCCode(TO_IFSC)
                    .amount(999.99)
                    .build();
            when(transactionMapper.toDto(any(TransactionEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(sampleDto());

            // Act
            Transaction result = transactionService.transferMoney(transfer);

            // Assert
            assertNotNull(result);
            ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionRepository, times(1)).save(captor.capture());
            TransactionEntity saved = captor.getValue();
            assertEquals(FROM_ACCOUNT, saved.getFromAccountNumber());
            assertEquals(TO_ACCOUNT, saved.getToAccountNumber());
            assertEquals(TransactionType.TRANSFER, saved.getType());
            assertEquals("transaction-service-transfer", saved.getCreatedBy());
        }

        @Test
        @DisplayName("Should promote OPENED transactions to INPROGRESS and return mapped events")
        void getTransactionBatch_WithOpenedAndInProgress_UpdatesOpenedToInProgressAndReturnsEvents() {
            // Arrange
            TransactionEntity opened = TransactionEntity.builder()
                    .transactionNumber("txn-opened")
                    .fromAccountNumber(FROM_ACCOUNT)
                    .toAccountNumber(TO_ACCOUNT)
                    .amount(100.0)
                    .currency(Currency.INR)
                    .type(TransactionType.DEPOSIT)
                    .status(TransactionStatus.OPENED)
                    .build();
            TransactionEntity inProgress = TransactionEntity.builder()
                    .transactionNumber("txn-inprogress")
                    .status(TransactionStatus.INPROGRESS)
                    .build();
            when(transactionRepository.findByStatusIn(List.of(TransactionStatus.OPENED, TransactionStatus.INPROGRESS)))
                    .thenReturn(List.of(opened, inProgress));

            // Act
            List<TransactionEvent> events = transactionService.getTransactionBatch();

            // Assert
            assertEquals(2, events.size());
            // The OPENED entity must have been flipped to INPROGRESS before saveAll/mapping
            assertEquals(TransactionStatus.INPROGRESS, opened.getStatus());
            // The already-INPROGRESS entity must be left untouched
            assertEquals(TransactionStatus.INPROGRESS, inProgress.getStatus());

            verify(transactionRepository, times(1)).saveAll(List.of(opened, inProgress));

            TransactionEvent openedEvent = events.stream()
                    .filter(e -> "txn-opened".equals(e.getTransactionNumber()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(TransactionStatus.INPROGRESS.name(), openedEvent.getStatus());
            assertEquals(FROM_ACCOUNT.toString(), openedEvent.getFromAccountNumber());
            assertEquals(TransactionType.DEPOSIT.name(), openedEvent.getType());
        }

        @Test
        @DisplayName("Should return an empty list and skip saveAll when there is nothing to batch")
        void getTransactionBatch_EmptyList_ReturnsEmptyListAndSkipsSave() {
            // Arrange
            when(transactionRepository.findByStatusIn(List.of(TransactionStatus.OPENED, TransactionStatus.INPROGRESS)))
                    .thenReturn(List.of());

            // Act
            List<TransactionEvent> events = transactionService.getTransactionBatch();

            // Assert
            assertTrue(events.isEmpty());
            verify(transactionRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Should mark transaction COMPLETED when the response event reports COMPLETED")
        void updateTransactionResponseStatus_CompletedStatus_UpdatesEntity() {
            // Arrange
            TransactionEvent responseEvent = new TransactionEvent();
            responseEvent.setTransactionNumber(TXN_NUMBER);
            responseEvent.setStatus(TransactionStatus.COMPLETED.name());

            when(transactionRepository.findTransactionByTransactionNumber(TXN_NUMBER))
                    .thenReturn(Optional.of(sampleEntity));

            // Act
            transactionService.updateTransactionResponseStatus(responseEvent);

            // Assert
            assertEquals(TransactionStatus.COMPLETED, sampleEntity.getStatus());
            verify(transactionRepository, times(1)).save(sampleEntity);
        }

        @Test
        @DisplayName("Should mark transaction FAILED when the response event reports FAILED")
        void updateTransactionResponseStatus_FailedStatus_UpdatesEntity() {
            // Arrange
            TransactionEvent responseEvent = new TransactionEvent();
            responseEvent.setTransactionNumber(TXN_NUMBER);
            responseEvent.setStatus(TransactionStatus.FAILED.name());

            when(transactionRepository.findTransactionByTransactionNumber(TXN_NUMBER))
                    .thenReturn(Optional.of(sampleEntity));

            // Act
            transactionService.updateTransactionResponseStatus(responseEvent);

            // Assert
            assertEquals(TransactionStatus.FAILED, sampleEntity.getStatus());
            verify(transactionRepository, times(1)).save(sampleEntity);
        }

        @Test
        @DisplayName("Should create a WITHDRAW transaction from a DEBIT payment event")
        void processPaymentEvent_DebitPaymentType_CreatesWithdrawTransaction() {
            // Arrange
            PaymentEvent event = new PaymentEvent();
            event.setPaymentId("pay-debit-001");
            event.setFromAccountNumber(FROM_ACCOUNT.toString());
            event.setToAccountNumber(TO_ACCOUNT.toString());
            event.setFromIFSCCode(FROM_IFSC);
            event.setToIFSCCode(TO_IFSC);
            event.setAmount(250.0);
            event.setPaymentType(PaymentType.DEBIT);

            when(transactionRepository.existsByReferenceNumber("pay-debit-001")).thenReturn(false);

            // Act
            transactionService.processPaymentEvent(event);

            // Assert
            ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionRepository, times(1)).save(captor.capture());
            TransactionEntity saved = captor.getValue();
            assertEquals(FROM_ACCOUNT, saved.getFromAccountNumber());
            assertEquals(TO_ACCOUNT, saved.getToAccountNumber());
            assertEquals(TransactionType.WITHDRAW, saved.getType());
            assertEquals(TransactionStatus.OPENED, saved.getStatus());
            assertEquals("payment-service", saved.getCreatedBy());
            assertEquals("pay-debit-001", saved.getReferenceNumber());
        }

        @Test
        @DisplayName("Should create a TRANSFER transaction from a TRANSFER payment event")
        void processPaymentEvent_TransferPaymentType_CreatesTransferTransaction() {
            // Arrange
            PaymentEvent event = new PaymentEvent();
            event.setPaymentId("pay-transfer-001");
            event.setFromAccountNumber(FROM_ACCOUNT.toString());
            event.setToAccountNumber(TO_ACCOUNT.toString());
            event.setFromIFSCCode(FROM_IFSC);
            event.setToIFSCCode(TO_IFSC);
            event.setAmount(600.0);
            event.setPaymentType(PaymentType.TRANSFER);

            when(transactionRepository.existsByReferenceNumber("pay-transfer-001")).thenReturn(false);

            // Act
            transactionService.processPaymentEvent(event);

            // Assert
            ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionRepository, times(1)).save(captor.capture());
            assertEquals(TransactionType.TRANSFER, captor.getValue().getType());
            assertEquals(TO_ACCOUNT, captor.getValue().getToAccountNumber());
        }

        @Test
        @DisplayName("Should default to TRANSFER type when the payment event has a null paymentType")
        void processPaymentEvent_NullPaymentType_DefaultsToTransferType() {
            // Arrange
            PaymentEvent event = new PaymentEvent();
            event.setPaymentId("pay-null-type-001");
            event.setFromAccountNumber(FROM_ACCOUNT.toString());
            event.setToAccountNumber(TO_ACCOUNT.toString());
            event.setAmount(50.0);
            event.setPaymentType(null);

            when(transactionRepository.existsByReferenceNumber("pay-null-type-001")).thenReturn(false);

            // Act
            transactionService.processPaymentEvent(event);

            // Assert
            ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionRepository, times(1)).save(captor.capture());
            assertEquals(TransactionType.TRANSFER, captor.getValue().getType());
        }
    }

    // ---------------------------------------------------------------------
    // Edge Case / Validation Tests
    // ---------------------------------------------------------------------

    @Nested
    @DisplayName("Edge Case and Validation scenarios")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should throw EntityNotFoundException when transaction number does not exist")
        void getTransactionByTransactionNumber_NotFound_ThrowsEntityNotFoundException() {
            // Arrange
            when(transactionRepository.findTransactionByTransactionNumber("does-not-exist"))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> transactionService.getTransactionByTransactionNumber("does-not-exist"));

            verifyNoInteractions(transactionMapper);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when updating status of a non-existent transaction")
        void updateTransactionStatus_NotFound_ThrowsEntityNotFoundException() {
            // Arrange
            when(transactionRepository.findTransactionByTransactionNumber("does-not-exist"))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> transactionService.updateTransactionStatus("does-not-exist", TransactionStatus.COMPLETED));

            verify(transactionRepository, never()).save(any(TransactionEntity.class));
            verifyNoInteractions(transactionMapper);
        }

        @Test
        @DisplayName("Should silently no-op when a response event references an unknown transaction number")
        void updateTransactionResponseStatus_TransactionNotFound_NoActionTaken() {
            // Arrange
            TransactionEvent responseEvent = new TransactionEvent();
            responseEvent.setTransactionNumber("unknown-txn");
            responseEvent.setStatus(TransactionStatus.COMPLETED.name());

            when(transactionRepository.findTransactionByTransactionNumber("unknown-txn"))
                    .thenReturn(Optional.empty());

            // Act & Assert - must NOT throw, method is designed to log and return
            assertDoesNotThrow(() -> transactionService.updateTransactionResponseStatus(responseEvent));

            verify(transactionRepository, never()).save(any(TransactionEntity.class));
        }

        @Test
        @DisplayName("Should not persist any change when the response event status is neither COMPLETED nor FAILED")
        void updateTransactionResponseStatus_NonTerminalStatus_DoesNotSave() {
            // Arrange
            TransactionEvent responseEvent = new TransactionEvent();
            responseEvent.setTransactionNumber(TXN_NUMBER);
            responseEvent.setStatus(TransactionStatus.INPROGRESS.name());

            when(transactionRepository.findTransactionByTransactionNumber(TXN_NUMBER))
                    .thenReturn(Optional.of(sampleEntity));

            // Act
            transactionService.updateTransactionResponseStatus(responseEvent);

            // Assert - status on the entity is untouched and nothing is persisted
            assertEquals(TransactionStatus.OPENED, sampleEntity.getStatus());
            verify(transactionRepository, never()).save(any(TransactionEntity.class));
        }

        @Test
        @DisplayName("Should skip creating a transaction when the payment event is a duplicate")
        void processPaymentEvent_DuplicatePaymentId_SkipsCreation() {
            // Arrange
            PaymentEvent event = new PaymentEvent();
            event.setPaymentId("pay-duplicate-001");
            event.setFromAccountNumber(FROM_ACCOUNT.toString());
            event.setPaymentType(PaymentType.DEBIT);

            when(transactionRepository.existsByReferenceNumber("pay-duplicate-001")).thenReturn(true);

            // Act
            transactionService.processPaymentEvent(event);

            // Assert
            verify(transactionRepository, never()).save(any(TransactionEntity.class));
        }

        @Test
        @DisplayName("CREDIT payment events overwrite toAccountNumber with the fromAccountNumber value (documented existing behavior)")
        void processPaymentEvent_CreditPaymentType_OverwritesToAccountWithFromAccount() {
            // Arrange
            PaymentEvent event = new PaymentEvent();
            event.setPaymentId("pay-credit-001");
            event.setFromAccountNumber(FROM_ACCOUNT.toString());
            event.setToAccountNumber(TO_ACCOUNT.toString());
            event.setToIFSCCode(TO_IFSC);
            event.setAmount(80.0);
            event.setPaymentType(PaymentType.CREDIT);

            when(transactionRepository.existsByReferenceNumber("pay-credit-001")).thenReturn(false);

            // Act
            transactionService.processPaymentEvent(event);

            // Assert
            ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionRepository, times(1)).save(captor.capture());
            TransactionEntity saved = captor.getValue();
            assertEquals(TransactionType.DEPOSIT, saved.getType());
            // NOTE: per current service logic, toAccountNumber ends up equal to the
            // FROM account for CREDIT events - not the original toAccountNumber on the event.
            assertEquals(FROM_ACCOUNT, saved.getToAccountNumber());
            assertEquals(TO_IFSC, saved.getToIFSCCode());
        }
    }
}
