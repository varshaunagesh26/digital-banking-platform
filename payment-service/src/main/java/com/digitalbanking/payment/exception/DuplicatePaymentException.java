package com.digitalbanking.payment.exception;

public class DuplicatePaymentException extends RuntimeException {
    public DuplicatePaymentException(String idempotencyKey) {
        super("Payment with idempotency key already exists: " + idempotencyKey);
    }
}
