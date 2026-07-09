package com.db.payment.controller;

import com.digital.backend.model.paymentservice.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


public interface PaymentControllerSpec {

    @PostMapping(path = "/api/v1/payment/deposit/{fromAccountNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return deposited amount", tags = {"payment"})
    @ApiResponse(
            responseCode = "200",
            description = "Returned payment status",
            content = @Content(schema = @Schema(implementation = Payment.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<Payment> depositMoney(
            @Parameter(description = "Account Number", required = true)
            @PathVariable String fromAccountNumber,
            @RequestBody Payment deposit);
}
