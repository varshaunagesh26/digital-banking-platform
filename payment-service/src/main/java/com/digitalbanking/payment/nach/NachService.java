package com.digitalbanking.payment.nach;

import com.digitalbanking.payment.dto.PaymentEvent;
import com.digitalbanking.payment.entity.PaymentEntity;

public interface NachService {
    void performInterBankTransfer(PaymentEntity paymentEntity);
}
