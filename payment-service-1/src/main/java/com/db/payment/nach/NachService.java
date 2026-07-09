package com.db.payment.nach;

import com.db.payment.entity.PaymentEntity;

public interface NachService {
    void performInterBankTransfer(PaymentEntity paymentEntity);

}
