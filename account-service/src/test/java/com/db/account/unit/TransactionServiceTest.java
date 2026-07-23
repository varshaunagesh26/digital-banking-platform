package com.db.account.unit;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.BranchEntity;
import com.db.account.entity.EventHistory;
import com.db.account.repository.AccountRepository;
import com.db.account.repository.BranchRepository;
import com.db.account.repository.EventHistoryRepository;
import com.db.account.response.publisher.TransactionEventResponsePublisher;
import com.db.account.service.TransactionService;
import com.digital.backend.events.TransactionEvent;
import com.digital.backend.model.enums.EventStatus;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TransactionService}.
 * <p>
 * Pure Mockito tests (no Spring context loaded). Downstream collaborators
 * (repositories, the Kafka response publisher) are mocked; only the service
 * logic under test is real.
 * <p>
 * <b>Assumption:</b> {@code TransactionEvent}'s source wasn't provided, so
 * fixtures are built via a no-arg constructor + setters, inferred from the
 * getters/setters referenced in {@code TransactionService} and
 * {@code TransactionListener} (transactionNumber, amount, type, status,
 * fromAccountNumber, toAccountNumber, fromIFSCCode, toIFSCCode - amount and
 * account numbers are Strings, parsed inside the service). If the real class
 * differs, share it and I'll adjust the fixtures.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionEventResponsePublisher transactionPublisher;

    @Mock
    private EventHistoryRepository eventHistoryRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private TransactionService transactionService;

    private static final String TRANSACTION_NUMBER = "TXN-1001";
    private static final Long FROM_ACCOUNT_NUMBER = 111L;
    private static final Long TO_ACCOUNT_NUMBER = 222L;
    private static final String FROM_IFSC = "IFSC0001";
    private static final String TO_IFSC = "IFSC0002";

    private AccountEntity fromAccount;
    private AccountEntity toAccount;
    private BranchEntity branchEntity;

    @BeforeEach
    void setUp() {
        branchEntity = BranchEntity.builder()
                .id(1L)
                .branchCode(100L)
                .branchName("Main Branch")
                .branchIFSC(FROM_IFSC)
                .isActive(true)
                .build();

        fromAccount = AccountEntity.builder()
                .id(1L)
                .accountNumber(FROM_ACCOUNT_NUMBER)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .isActive(true)
                .build();

        toAccount = AccountEntity.builder()
                .id(2L)
                .accountNumber(TO_ACCOUNT_NUMBER)
                .accountType("SAVINGS")
                .accountBalance(500.0)
                .isActive(true)
                .build();
    }

    private TransactionEvent depositEvent(String amount) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionNumber(TRANSACTION_NUMBER);
        event.setType(TransactionType.DEPOSIT.name());
        event.setAmount(amount);
        event.setToAccountNumber(String.valueOf(TO_ACCOUNT_NUMBER));
        event.setToIFSCCode(TO_IFSC);
        return event;
    }

    private TransactionEvent withdrawalEvent(String amount) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionNumber(TRANSACTION_NUMBER);
        event.setType(TransactionType.WITHDRAW.name());
        event.setAmount(amount);
        event.setFromAccountNumber(String.valueOf(FROM_ACCOUNT_NUMBER));
        event.setFromIFSCCode(FROM_IFSC);
        return event;
    }

    private TransactionEvent transferEvent(String amount) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionNumber(TRANSACTION_NUMBER);
        event.setType(TransactionType.TRANSFER.name());
        event.setAmount(amount);
        event.setFromAccountNumber(String.valueOf(FROM_ACCOUNT_NUMBER));
        event.setToAccountNumber(String.valueOf(TO_ACCOUNT_NUMBER));
        event.setFromIFSCCode(FROM_IFSC);
        event.setToIFSCCode(TO_IFSC);
        return event;
    }

    // ------------------------------------------------------------------
    // processDeposit
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("processDeposit")
    class ProcessDeposit {

        @Test
        @DisplayName("Happy path: credits the account and marks the transaction COMPLETED")
        void shouldCreditAccountSuccessfully() {
            // Arrange
            TransactionEvent event = depositEvent("250.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(TO_IFSC)).thenReturn(Optional.of(branchEntity));
            when(accountRepository.findAccountByAccountNumber(TO_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(toAccount));

            // Act
            transactionService.processDeposit(event);

            // Assert
            ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
            verify(accountRepository, times(1)).save(captor.capture());
            assertThat(captor.getValue().getAccountBalance()).isEqualTo(750.0);
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.COMPLETED.toString());
            verify(transactionPublisher, times(1)).publishResponseEvent(event);
        }

        @Test
        @DisplayName("Edge case: already COMPLETED - skips re-processing and just republishes")
        void shouldSkipWhenAlreadyCompleted() {
            // Arrange
            TransactionEvent event = depositEvent("250.0");
            EventHistory existingHistory = EventHistory.builder()
                    .transactionNumber(TRANSACTION_NUMBER)
                    .status(TransactionStatus.COMPLETED)
                    .build();
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.of(existingHistory));

            // Act
            transactionService.processDeposit(event);

            // Assert
            verify(accountRepository, never()).findAccountByAccountNumber(any());
            verify(accountRepository, never()).save(any());
            verify(transactionPublisher, times(1)).publishResponseEvent(event);
        }

        @Test
        @DisplayName("Edge case: destination branch IFSC not found - marks FAILED without crediting")
        void shouldFailWhenBranchInvalid() {
            // Arrange
            TransactionEvent event = depositEvent("250.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(TO_IFSC)).thenReturn(Optional.empty());

            // Act
            transactionService.processDeposit(event);

            // Assert
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.FAILED.toString());
            verify(accountRepository, never()).save(any());
            verify(transactionPublisher, times(1)).publishResponseEvent(event);
        }

        @Test
        @DisplayName("Edge case: account not found - throws RuntimeException")
        void shouldThrowWhenAccountNotFound() {
            // Arrange
            TransactionEvent event = depositEvent("250.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(TO_IFSC)).thenReturn(Optional.of(branchEntity));
            when(accountRepository.findAccountByAccountNumber(TO_ACCOUNT_NUMBER))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> transactionService.processDeposit(event));
            verify(transactionPublisher, never()).publishResponseEvent(any());
        }

        @Test
        @DisplayName("Edge case: destination account inactive - marks FAILED without crediting")
        void shouldFailWhenAccountInactive() {
            // Arrange
            toAccount.setIsActive(false);
            TransactionEvent event = depositEvent("250.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(TO_IFSC)).thenReturn(Optional.of(branchEntity));
            when(accountRepository.findAccountByAccountNumber(TO_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(toAccount));

            // Act
            transactionService.processDeposit(event);

            // Assert
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.FAILED.toString());
            verify(accountRepository, never()).save(any());
        }
    }

    // ------------------------------------------------------------------
    // processWithdrawal
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("processWithdrawal")
    class ProcessWithdrawal {

        @Test
        @DisplayName("Happy path: debits the account and marks the transaction COMPLETED")
        void shouldDebitAccountSuccessfully() {
            // Arrange
            TransactionEvent event = withdrawalEvent("300.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(FROM_IFSC)).thenReturn(Optional.of(branchEntity));
            when(accountRepository.findAccountByAccountNumber(FROM_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(fromAccount));

            // Act
            transactionService.processWithdrawal(event);

            // Assert
            ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
            verify(accountRepository, times(1)).save(captor.capture());
            assertThat(captor.getValue().getAccountBalance()).isEqualTo(700.0);
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.COMPLETED.toString());
            verify(transactionPublisher, times(1)).publishResponseEvent(event);
        }

        @Test
        @DisplayName("Edge case: insufficient balance - marks FAILED without debiting")
        void shouldFailWhenBalanceInsufficient() {
            // Arrange
            TransactionEvent event = withdrawalEvent("5000.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(FROM_IFSC)).thenReturn(Optional.of(branchEntity));
            when(accountRepository.findAccountByAccountNumber(FROM_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(fromAccount));

            // Act
            transactionService.processWithdrawal(event);

            // Assert
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.FAILED.toString());
            verify(accountRepository, never()).save(any());
            verify(transactionPublisher, times(1)).publishResponseEvent(event);
        }

        @Test
        @DisplayName("Edge case: source branch IFSC not found - marks FAILED without debiting")
        void shouldFailWhenBranchInvalid() {
            // Arrange
            TransactionEvent event = withdrawalEvent("300.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(FROM_IFSC)).thenReturn(Optional.empty());

            // Act
            transactionService.processWithdrawal(event);

            // Assert
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.FAILED.toString());
            verify(accountRepository, never()).findAccountByAccountNumber(any());
        }

        @Test
        @DisplayName("Edge case: already COMPLETED - skips re-processing and just republishes")
        void shouldSkipWhenAlreadyCompleted() {
            // Arrange
            TransactionEvent event = withdrawalEvent("300.0");
            EventHistory existingHistory = EventHistory.builder()
                    .transactionNumber(TRANSACTION_NUMBER)
                    .status(TransactionStatus.COMPLETED)
                    .build();
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.of(existingHistory));

            // Act
            transactionService.processWithdrawal(event);

            // Assert
            verify(accountRepository, never()).save(any());
            verify(transactionPublisher, times(1)).publishResponseEvent(event);
        }
    }

    // ------------------------------------------------------------------
    // processTransfer
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("processTransfer")
    class ProcessTransfer {

        @Test
        @DisplayName("Happy path: moves funds between accounts and marks COMPLETED")
        void shouldTransferFundsSuccessfully() {
            // Arrange
            TransactionEvent event = transferEvent("400.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(FROM_IFSC)).thenReturn(Optional.of(branchEntity));
            when(branchRepository.findByBranchIFSC(TO_IFSC)).thenReturn(Optional.of(branchEntity));
            when(accountRepository.findAccountByAccountNumber(FROM_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(fromAccount));
            when(accountRepository.findAccountByAccountNumber(TO_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(toAccount));

            // Act
            transactionService.processTransfer(event);

            // Assert
            assertThat(fromAccount.getAccountBalance()).isEqualTo(600.0);
            assertThat(toAccount.getAccountBalance()).isEqualTo(900.0);
            verify(accountRepository, times(1)).save(fromAccount);
            verify(accountRepository, times(1)).save(toAccount);
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.COMPLETED.toString());
            verify(transactionPublisher, times(1)).publishResponseEvent(event);
        }

        @Test
        @DisplayName("Edge case: sender branch IFSC not found - marks FAILED, no accounts touched")
        void shouldFailWhenSenderBranchInvalid() {
            // Arrange
            TransactionEvent event = transferEvent("400.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(FROM_IFSC)).thenReturn(Optional.empty());

            // Act
            transactionService.processTransfer(event);

            // Assert
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.FAILED.toString());
            verify(accountRepository, never()).findAccountByAccountNumber(any());
        }

        @Test
        @DisplayName("Edge case: receiver branch IFSC not found - marks FAILED, no accounts touched")
        void shouldFailWhenReceiverBranchInvalid() {
            // Arrange
            TransactionEvent event = transferEvent("400.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(FROM_IFSC)).thenReturn(Optional.of(branchEntity));
            when(branchRepository.findByBranchIFSC(TO_IFSC)).thenReturn(Optional.empty());

            // Act
            transactionService.processTransfer(event);

            // Assert
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.FAILED.toString());
            verify(accountRepository, never()).findAccountByAccountNumber(any());
        }

        @Test
        @DisplayName("Edge case: insufficient sender balance - marks FAILED without moving funds")
        void shouldFailWhenBalanceInsufficient() {
            // Arrange
            TransactionEvent event = transferEvent("5000.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(FROM_IFSC)).thenReturn(Optional.of(branchEntity));
            when(branchRepository.findByBranchIFSC(TO_IFSC)).thenReturn(Optional.of(branchEntity));
            when(accountRepository.findAccountByAccountNumber(FROM_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(fromAccount));
            when(accountRepository.findAccountByAccountNumber(TO_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(toAccount));

            // Act
            transactionService.processTransfer(event);

            // Assert
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.FAILED.toString());
            verify(accountRepository, never()).save(any());
        }

        @Test
        @DisplayName("Edge case: sender or receiver account inactive - marks FAILED without moving funds")
        void shouldFailWhenEitherAccountInactive() {
            // Arrange
            fromAccount.setIsActive(false);
            TransactionEvent event = transferEvent("400.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(FROM_IFSC)).thenReturn(Optional.of(branchEntity));
            when(branchRepository.findByBranchIFSC(TO_IFSC)).thenReturn(Optional.of(branchEntity));
            when(accountRepository.findAccountByAccountNumber(FROM_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(fromAccount));
            when(accountRepository.findAccountByAccountNumber(TO_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(toAccount));

            // Act
            transactionService.processTransfer(event);

            // Assert
            assertThat(event.getStatus()).isEqualTo(TransactionStatus.FAILED.toString());
            verify(accountRepository, never()).save(any());
        }

        @Test
        @DisplayName("Edge case: receiver account not found - throws RuntimeException")
        void shouldThrowWhenReceiverAccountNotFound() {
            // Arrange
            TransactionEvent event = transferEvent("400.0");
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByBranchIFSC(FROM_IFSC)).thenReturn(Optional.of(branchEntity));
            when(branchRepository.findByBranchIFSC(TO_IFSC)).thenReturn(Optional.of(branchEntity));
            when(accountRepository.findAccountByAccountNumber(FROM_ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(fromAccount));
            when(accountRepository.findAccountByAccountNumber(TO_ACCOUNT_NUMBER))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> transactionService.processTransfer(event));
            verify(transactionPublisher, never()).publishResponseEvent(any());
        }

        @Test
        @DisplayName("Edge case: already COMPLETED - skips re-processing and just republishes")
        void shouldSkipWhenAlreadyCompleted() {
            // Arrange
            TransactionEvent event = transferEvent("400.0");
            EventHistory existingHistory = EventHistory.builder()
                    .transactionNumber(TRANSACTION_NUMBER)
                    .status(TransactionStatus.COMPLETED)
                    .build();
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.of(existingHistory));

            // Act
            transactionService.processTransfer(event);

            // Assert
            verify(accountRepository, never()).findAccountByAccountNumber(any());
            verify(transactionPublisher, times(1)).publishResponseEvent(event);
        }
    }

    // ------------------------------------------------------------------
    // setEventHistory
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("setEventHistory")
    class SetEventHistory {

        @Test
        @DisplayName("Happy path: persists a new event history row with RESPONSE event status")
        void shouldPersistNewEventHistory() throws Exception {
            // Arrange
            TransactionEvent event = depositEvent("250.0");

            // Act
            transactionService.setEventHistory(event, TransactionType.DEPOSIT, TransactionStatus.INPROGRESS);

            // Assert
            ArgumentCaptor<EventHistory> captor = ArgumentCaptor.forClass(EventHistory.class);
            verify(eventHistoryRepository, times(1)).save(captor.capture());
            assertThat(captor.getValue().getTransactionNumber()).isEqualTo(TRANSACTION_NUMBER);
            assertThat(captor.getValue().getType()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(captor.getValue().getStatus()).isEqualTo(TransactionStatus.INPROGRESS);
            assertThat(captor.getValue().getEventStatus()).isEqualTo(EventStatus.RESPONSE);
        }
    }

    // ------------------------------------------------------------------
    // isAlreadyProcessed
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("isAlreadyProcessed")
    class IsAlreadyProcessed {

        @Test
        @DisplayName("Happy path: returns true when history status is COMPLETED")
        void shouldReturnTrueWhenCompleted() {
            // Arrange
            EventHistory history = EventHistory.builder()
                    .transactionNumber(TRANSACTION_NUMBER)
                    .status(TransactionStatus.COMPLETED)
                    .build();
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.of(history));

            // Act & Assert
            assertThat(transactionService.isAlreadyProcessed(TRANSACTION_NUMBER)).isTrue();
        }

        @Test
        @DisplayName("Happy path: returns true when history status is FAILED")
        void shouldReturnTrueWhenFailed() {
            // Arrange
            EventHistory history = EventHistory.builder()
                    .transactionNumber(TRANSACTION_NUMBER)
                    .status(TransactionStatus.FAILED)
                    .build();
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.of(history));

            // Act & Assert
            assertThat(transactionService.isAlreadyProcessed(TRANSACTION_NUMBER)).isTrue();
        }

        @Test
        @DisplayName("Edge case: returns false when history status is INPROGRESS")
        void shouldReturnFalseWhenInProgress() {
            // Arrange
            EventHistory history = EventHistory.builder()
                    .transactionNumber(TRANSACTION_NUMBER)
                    .status(TransactionStatus.INPROGRESS)
                    .build();
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.of(history));

            // Act & Assert
            assertThat(transactionService.isAlreadyProcessed(TRANSACTION_NUMBER)).isFalse();
        }

        @Test
        @DisplayName("Edge case: returns false when no history exists")
        void shouldReturnFalseWhenNoHistoryExists() {
            // Arrange
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThat(transactionService.isAlreadyProcessed(TRANSACTION_NUMBER)).isFalse();
        }
    }

    // ------------------------------------------------------------------
    // republishExistingResult
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("republishExistingResult")
    class RepublishExistingResult {

        @Test
        @DisplayName("Happy path: republishes a response event built from existing history")
        void shouldRepublishExistingResult() {
            // Arrange
            EventHistory history = EventHistory.builder()
                    .transactionNumber(TRANSACTION_NUMBER)
                    .type(TransactionType.DEPOSIT)
                    .status(TransactionStatus.COMPLETED)
                    .build();
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.of(history));

            // Act
            transactionService.republishExistingResult(TRANSACTION_NUMBER);

            // Assert
            ArgumentCaptor<TransactionEvent> captor = ArgumentCaptor.forClass(TransactionEvent.class);
            verify(transactionPublisher, times(1)).publishResponseEvent(captor.capture());
            assertThat(captor.getValue().getTransactionNumber()).isEqualTo(TRANSACTION_NUMBER);
            assertThat(captor.getValue().getStatus()).isEqualTo(TransactionStatus.COMPLETED.toString());
            assertThat(captor.getValue().getType()).isEqualTo(TransactionType.DEPOSIT.toString());
        }

        @Test
        @DisplayName("Edge case: does nothing when no history exists for the transaction")
        void shouldDoNothingWhenNoHistoryExists() {
            // Arrange
            when(eventHistoryRepository.findEventHistoryByTransactionNumber(TRANSACTION_NUMBER))
                    .thenReturn(Optional.empty());

            // Act
            transactionService.republishExistingResult(TRANSACTION_NUMBER);

            // Assert
            verify(transactionPublisher, never()).publishResponseEvent(any());
        }
    }
}
