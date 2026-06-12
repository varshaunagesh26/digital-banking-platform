package com.digital.backend.model;

import com.digital.backend.model.enums.Currency;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Schema(name = "transactionId")
    private String transactionNumber;

    @Schema(name = "fromAccountNumber")
    private Long fromAccountNumber;

    @Schema(name = "toAccountNumber")
    private Long toAccountNumber;

    @Schema(name = "amount")
    private Double amount;

    @Schema(name = "currency")
    private Currency currency;

    @Schema(name = "type")
    private TransactionType type;

    @Schema(name = "status")
    private TransactionStatus status;

    @Schema(name = "referenceNumber")
    private String referenceNumber;

    @Schema(name = "timestamp")
    private Date timestamp;
}
