package com.db.payment;

import com.db.payment.entity.PaymentEntity;
import com.db.payment.mapper.CycleAvoidMappingContext;
import com.db.payment.mapper.PaymentMapper;
import com.db.payment.nach.NachService;
import com.db.payment.publisher.PaymentEventPublisher;
import com.db.payment.repository.PaymentRepository;
import com.db.payment.service.PaymentService;
import com.digital.backend.model.enums.PaymentOperation;
import com.digital.backend.model.enums.PaymentStatus;
import com.digital.backend.model.enums.PaymentType;
import com.digital.backend.model.paymentservice.AccountInfo;
import com.digital.backend.model.paymentservice.Payment;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PaymentService}.
 *
 * NOTE: The {@code Payment} / {@code PaymentAccountInfo} DTOs were not supplied in the
 * provided source. These tests assume they expose a Lombok-style {@code @Builder}
 * (consistent with {@code PaymentEntity} in this codebase). If the real DTOs instead
 * expose plain setters, swap the {@code .builder()...build()} calls below for
 * {@code new Payment()} + setters.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @Mock
    private NachService nachService;

    @InjectMocks
    private PaymentService paymentService;

    private static final String SBI_FROM_IFSC = "SBIN0001234";
    private static final String SBI_TO_IFSC = "SBIN0005678";
    private static final String NON_SBI_IFSC = "HDFC0001234";
    private static final String FROM_ACCOUNT = "1000200030004000";
    private static final String TO_ACCOUNT = "9000800070006000";

    private Payment.PaymentBuilder basePaymentBuilder;

    @BeforeEach
    void setUp() {
        // Arrange (shared): a baseline valid "from" account payload reused across tests
        basePaymentBuilder = Payment.builder()
                .amount(1000.00)
                .paymentAccountInfo(
                        AccountInfo.builder()
                                .fromAccountNumber(FROM_ACCOUNT)
                                .fromIFSCCode(SBI_FROM_IFSC)
                                .build()
                );
    }

    // ---------------------------------------------------------------------
    // Happy Path Tests
    // ---------------------------------------------------------------------

    @Nested
    @DisplayName("Happy Path scenarios")
    class HappyPathTests {

        @Test
        @DisplayName("Should create a non-transfer payment (e.g. deposit) without invoking NACH")
        void createPayment_NonTransferType_SkipsTransferValidationAndNach() {
            // Arrange
            Payment inputPayment = basePaymentBuilder
                    .paymentType(PaymentType.CREDIT)
                    .paymentOperation(PaymentOperation.NEFT)
                    .build();

            Payment expectedDto = Payment.builder()
                    .amount(1000.00)
                    .paymentType(PaymentType.CREDIT)
                    .build();

            when(paymentMapper.toDto(any(PaymentEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(expectedDto);

            // Act
            Payment result = paymentService.createPayment(inputPayment);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);

            verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
            verify(nachService, never()).performInterBankTransfer(any(PaymentEntity.class));
            verify(paymentEventPublisher, times(1)).publishRequestEvent(any(PaymentEvent.class));
            verify(paymentMapper, times(1))
                    .toDto(any(PaymentEntity.class), any(CycleAvoidMappingContext.class));
        }

        @Test
        @DisplayName("Should process an intra-bank (SBI to SBI) transfer without invoking NACH")
        void createPayment_IntraBankTransfer_DoesNotInvokeNach() {
            // Arrange
            Payment inputPayment = basePaymentBuilder
                    .paymentType(PaymentType.TRANSFER)
                    .paymentOperation(PaymentOperation.RTGS)
                    .paymentAccountInfo(
                            AccountInfo.builder()
                                    .fromAccountNumber(FROM_ACCOUNT)
                                    .fromIFSCCode(SBI_FROM_IFSC)
                                    .toAccountNumber(TO_ACCOUNT)
                                    .toIFSCCode(SBI_TO_IFSC)
                                    .build()
                    )
                    .build();

            Payment expectedDto = Payment.builder()
                    .amount(1000.00)
                    .paymentType(PaymentType.TRANSFER)
                    .build();

            when(paymentMapper.toDto(any(PaymentEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(expectedDto);

            // Act
            Payment result = paymentService.createPayment(inputPayment);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);

            verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
            // Intra-bank: NACH must never be called
            verify(nachService, never()).performInterBankTransfer(any(PaymentEntity.class));
            verify(paymentEventPublisher, times(1)).publishRequestEvent(any(PaymentEvent.class));
        }

        @Test
        @DisplayName("Should process an inter-bank transfer by routing through NachService")
        void createPayment_InterBankTransfer_InvokesNachService() {
            // Arrange
            Payment inputPayment = basePaymentBuilder
                    .paymentType(PaymentType.TRANSFER)
                    .paymentOperation(PaymentOperation.RTGS)
                    .paymentAccountInfo(
                            AccountInfo.builder()
                                    .fromAccountNumber(FROM_ACCOUNT)
                                    .fromIFSCCode(SBI_FROM_IFSC)
                                    .toAccountNumber(TO_ACCOUNT)
                                    .toIFSCCode(NON_SBI_IFSC)
                                    .build()
                    )
                    .build();

            Payment expectedDto = Payment.builder()
                    .amount(1000.00)
                    .paymentType(PaymentType.TRANSFER)
                    .build();

            when(paymentMapper.toDto(any(PaymentEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(expectedDto);

            // Act
            Payment result = paymentService.createPayment(inputPayment);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);

            ArgumentCaptor<PaymentEntity> entityCaptor = ArgumentCaptor.forClass(PaymentEntity.class);
            verify(nachService, times(1)).performInterBankTransfer(entityCaptor.capture());
            assertEquals(FROM_ACCOUNT, entityCaptor.getValue().getFromAccountNumber());
            assertEquals(NON_SBI_IFSC, entityCaptor.getValue().getToIFSCCode());

            verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
            verify(paymentEventPublisher, times(1)).publishRequestEvent(any(PaymentEvent.class));
        }

        @Test
        @DisplayName("Should build and publish a PaymentEvent whose fields mirror the saved entity")
        void createPayment_PublishesEventWithCorrectFields() {
            // Arrange
            Payment inputPayment = basePaymentBuilder
                    .paymentType(PaymentType.CREDIT)
                    .paymentOperation(PaymentOperation.NEFT)
                    .build();

            when(paymentMapper.toDto(any(PaymentEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(Payment.builder().build());

            // Act
            paymentService.createPayment(inputPayment);

            // Assert
            ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);
            verify(paymentEventPublisher, times(1)).publishRequestEvent(eventCaptor.capture());

            PaymentEvent publishedEvent = eventCaptor.getValue();
            assertEquals(FROM_ACCOUNT, publishedEvent.getFromAccountNumber());
            assertEquals(SBI_FROM_IFSC, publishedEvent.getFromIFSCCode());
            assertEquals(PaymentType.CREDIT, publishedEvent.getPaymentType());
            assertEquals(PaymentOperation.NEFT, publishedEvent.getPaymentOperation());
        }
    }

    // ---------------------------------------------------------------------
    // Edge Case / Validation Tests
    // ---------------------------------------------------------------------

    @Nested
    @DisplayName("Edge Case and Validation scenarios")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when paymentAccountInfo is null")
        void createPayment_NullPaymentAccountInfo_ThrowsIllegalArgumentException() {
            // Arrange
            Payment inputPayment = Payment.builder()
                    .amount(500.00)
                    .paymentAccountInfo(null)
                    .build();

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> paymentService.createPayment(inputPayment)
            );
            assertEquals("Missing from account details", exception.getMessage());

            verifyNoInteractions(paymentRepository, paymentEventPublisher, nachService, paymentMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when fromAccountNumber is null")
        void createPayment_NullFromAccountNumber_ThrowsIllegalArgumentException() {
            // Arrange
            Payment inputPayment = Payment.builder()
                    .amount(500.00)
                    .paymentAccountInfo(
                            AccountInfo.builder()
                                    .fromAccountNumber(null)
                                    .fromIFSCCode(SBI_FROM_IFSC)
                                    .build()
                    )
                    .build();

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                    () -> paymentService.createPayment(inputPayment));

            verifyNoInteractions(paymentRepository, paymentEventPublisher, nachService, paymentMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when fromIFSCCode is null")
        void createPayment_NullFromIfscCode_ThrowsIllegalArgumentException() {
            // Arrange
            Payment inputPayment = Payment.builder()
                    .amount(500.00)
                    .paymentAccountInfo(
                            AccountInfo.builder()
                                    .fromAccountNumber(FROM_ACCOUNT)
                                    .fromIFSCCode(null)
                                    .build()
                    )
                    .build();

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                    () -> paymentService.createPayment(inputPayment));

            verifyNoInteractions(paymentRepository, paymentEventPublisher, nachService, paymentMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when fromIFSCCode does not belong to SBI")
        void createPayment_NonSbiFromIfsc_ThrowsIllegalArgumentException() {
            // Arrange
            Payment inputPayment = Payment.builder()
                    .amount(500.00)
                    .paymentAccountInfo(
                            AccountInfo.builder()
                                    .fromAccountNumber(FROM_ACCOUNT)
                                    .fromIFSCCode(NON_SBI_IFSC)
                                    .build()
                    )
                    .build();

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> paymentService.createPayment(inputPayment)
            );
            assertEquals("From account must belong to SBI (IFSC starting with SBIN)", exception.getMessage());

            // Must fail fast, before any persistence occurs
            verifyNoInteractions(paymentRepository, paymentEventPublisher, nachService, paymentMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for TRANSFER missing toAccountNumber")
        void createPayment_TransferMissingToAccountNumber_ThrowsIllegalArgumentException() {
            // Arrange
            Payment inputPayment = basePaymentBuilder
                    .paymentType(PaymentType.TRANSFER)
                    .paymentOperation(PaymentOperation.RTGS)
                    .paymentAccountInfo(
                            AccountInfo.builder()
                                    .fromAccountNumber(FROM_ACCOUNT)
                                    .fromIFSCCode(SBI_FROM_IFSC)
                                    .toAccountNumber(null)
                                    .toIFSCCode(SBI_TO_IFSC)
                                    .build()
                    )
                    .build();

            // Act & Assert
            // Note: the entity is saved (INITIATED) BEFORE the to-account validation runs,
            // so save() is expected to have been invoked once even though the method
            // ultimately throws.
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> paymentService.createPayment(inputPayment)
            );
            assertEquals("toAccountNumber and toIFSCCode are required for a transfer", exception.getMessage());

            verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
            verifyNoInteractions(nachService, paymentEventPublisher, paymentMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for TRANSFER missing toIFSCCode")
        void createPayment_TransferMissingToIfscCode_ThrowsIllegalArgumentException() {
            // Arrange
            Payment inputPayment = basePaymentBuilder
                    .paymentType(PaymentType.TRANSFER)
                    .paymentOperation(PaymentOperation.RTGS)
                    .paymentAccountInfo(
                            AccountInfo.builder()
                                    .fromAccountNumber(FROM_ACCOUNT)
                                    .fromIFSCCode(SBI_FROM_IFSC)
                                    .toAccountNumber(TO_ACCOUNT)
                                    .toIFSCCode(null)
                                    .build()
                    )
                    .build();

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                    () -> paymentService.createPayment(inputPayment));

            verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
            verifyNoInteractions(nachService, paymentEventPublisher, paymentMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when toIFSCCode is shorter than 4 characters")
        void createPayment_TransferInvalidToIfscLength_ThrowsIllegalArgumentException() {
            // Arrange
            Payment inputPayment = basePaymentBuilder
                    .paymentType(PaymentType.TRANSFER)
                    .paymentOperation(PaymentOperation.RTGS)
                    .paymentAccountInfo(
                            AccountInfo.builder()
                                    .fromAccountNumber(FROM_ACCOUNT)
                                    .fromIFSCCode(SBI_FROM_IFSC)
                                    .toAccountNumber(TO_ACCOUNT)
                                    .toIFSCCode("SBI") // length 3, invalid
                                    .build()
                    )
                    .build();

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> paymentService.createPayment(inputPayment)
            );
            assertEquals("toIFSCCode is not a valid IFSC code", exception.getMessage());

            verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
            verifyNoInteractions(nachService, paymentEventPublisher, paymentMapper);
        }

        @Test
        @DisplayName("Should treat an IFSC shorter than 4 characters as non-SBI (fails fast on from-account check)")
        void createPayment_FromIfscShorterThanPrefix_ThrowsIllegalArgumentException() {
            // Arrange
            Payment inputPayment = Payment.builder()
                    .amount(500.00)
                    .paymentAccountInfo(
                            AccountInfo.builder()
                                    .fromAccountNumber(FROM_ACCOUNT)
                                    .fromIFSCCode("SBI") // length 3, cannot equal "SBIN"
                                    .build()
                    )
                    .build();

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                    () -> paymentService.createPayment(inputPayment));

            verifyNoInteractions(paymentRepository, paymentEventPublisher, nachService, paymentMapper);
        }

        @Test
        @DisplayName("Saved PaymentEntity should always start in INITIATED status")
        void createPayment_SavedEntity_HasInitiatedStatus() {
            // Arrange
            Payment inputPayment = basePaymentBuilder
                    .paymentType(PaymentType.CREDIT)
                    .paymentOperation(PaymentOperation.NEFT)
                    .build();

            when(paymentMapper.toDto(any(PaymentEntity.class), any(CycleAvoidMappingContext.class)))
                    .thenReturn(Payment.builder().build());

            // Act
            paymentService.createPayment(inputPayment);

            // Assert
            ArgumentCaptor<PaymentEntity> entityCaptor = ArgumentCaptor.forClass(PaymentEntity.class);
            verify(paymentRepository).save(entityCaptor.capture());
            assertEquals(PaymentStatus.INITIATED, entityCaptor.getValue().getPaymentStatus());
        }
    }
}