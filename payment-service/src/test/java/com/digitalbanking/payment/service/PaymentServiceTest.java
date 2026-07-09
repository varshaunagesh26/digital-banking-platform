package com.digitalbanking.payment.service;

import com.digitalbanking.payment.publisher.PaymentEventPublisher;
import com.digitalbanking.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentEventPublisher eventProducer;
    @Mock private PaymentProcessingService processingService;

    @InjectMocks private PaymentService paymentService;

    private PaymentRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = PaymentRequest.builder()
            .idempotencyKey("test-idem-key-001")
            .senderAccountId("ACC-001")
            .receiverAccountId("ACC-002")
            .amount(new BigDecimal("500.00"))
            .currency("EUR")
            .description("Test payment")
            .build();
    }

    @Test
    @DisplayName("Should initiate payment successfully for a new idempotency key")
    void shouldInitiatePaymentSuccessfully() {
        // Given
        when(paymentRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        Payment savedPayment = buildPayment(PaymentStatus.INITIATED);
        when(paymentRepository.save(any())).thenReturn(savedPayment);

        // When
        var response = paymentService.initiatePayment(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        verify(paymentRepository).save(any(Payment.class));
        verify(eventProducer).publishPaymentInitiated(any(Payment.class));
        verify(processingService).beginProcessing(any(Payment.class));
    }

    @Test
    @DisplayName("Should return existing payment on duplicate idempotency key")
    void shouldReturnExistingPaymentOnDuplicateKey() {
        // Given
        Payment existing = buildPayment(PaymentStatus.COMPLETED);
        when(paymentRepository.findByIdempotencyKey("test-idem-key-001"))
            .thenReturn(Optional.of(existing));

        // When
        var response = paymentService.initiatePayment(validRequest);

        // Then — no new payment saved, no new events published
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        verify(paymentRepository, never()).save(any());
        verify(eventProducer, never()).publishPaymentInitiated(any());
        verify(processingService, never()).beginProcessing(any());
    }

    @Test
    @DisplayName("Should throw PaymentNotFoundException for unknown payment ID")
    void shouldThrowForUnknownPaymentId() {
        UUID unknownId = UUID.randomUUID();
        when(paymentRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPayment(unknownId))
            .isInstanceOf(com.digitalbanking.payment.exception.PaymentNotFoundException.class);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Payment buildPayment(PaymentStatus status) {
        return Payment.builder()
            .id(UUID.randomUUID())
            .idempotencyKey(validRequest.getIdempotencyKey())
            .senderAccountId(validRequest.getSenderAccountId())
            .receiverAccountId(validRequest.getReceiverAccountId())
            .amount(validRequest.getAmount())
            .currency(validRequest.getCurrency())
            .status(status)
            .build();
    }
}
