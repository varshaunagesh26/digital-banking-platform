package com.db.transaction.controller;

import com.digital.backend.model.Transaction;
import com.digital.backend.model.TransactionInput;
import com.digital.backend.model.enums.TransactionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface TransactionControllerSpec {


    @GetMapping(path = "/api/v1/transactions/{transactionNumber}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return transaction for transactionNumber", tags = "transactions")
    @ApiResponse(
            responseCode = "200",
            description = "Return transactions for transactionNumber",
            content = @Content(schema = @Schema(implementation = TransactionControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<Transaction> getTransactionByTransactionNumber(
            @PathVariable String transactionNumber);


    @GetMapping(path = "/api/v1/transactions/{transactionStatus}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return all transactions for transactionStatus", tags = "transactions")
    @ApiResponse(
            responseCode = "200",
            description = "Return all transactions for transactionStatus",
            content = @Content(schema = @Schema(implementation = TransactionControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<List<Transaction>> getTransactionsByTransactionStatus(
            @PathVariable TransactionStatus transactionStatus);


    @GetMapping(path = "/api/v1/transactions",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return all the transactions", tags = "transactions")
    @ApiResponse(
            responseCode = "200",
            description = "Return all transactions",
            content = @Content(schema = @Schema(implementation = TransactionControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<List<Transaction>> getAllTransactions();


    @PatchMapping(path = "/api/v1/transactions/{transactionNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return the updated transaction", tags = "transaction")
    @ApiResponse(
            responseCode = "200",
            description = "Returned the updated transaction",
            content = @Content(schema = @Schema(implementation = TransactionControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<Transaction> updateTransactionStatus(
            @Parameter(description = "Transaction Number", required = true)
            @PathVariable String transactionNumber,
            @RequestParam TransactionStatus transactionStatus);

    /*****************************************************************************/

    @PostMapping(path = "/api/v1/transaction/deposit/{toAccountNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return deposited amount", tags = "transaction")
    @ApiResponse(
            responseCode = "200",
            description = "Returned transaction status",
            content = @Content(schema = @Schema(implementation = TransactionControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<Transaction> depositMoney(
            @Parameter(description = "Account Number", required = true)
            @RequestBody TransactionInput deposit);


    @PostMapping(path = "/api/v1/transaction/withdraw/{fromAccountNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retutn withdrawal money", tags = "transaction")
    @ApiResponse(
            responseCode = "200",
            description = "Return transaction status",
            content = @Content(schema = @Schema(implementation = TransactionControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<Transaction> withdrawMoney(
            @Parameter(description = "Account number", required = true)
            @RequestBody TransactionInput withdraw);

    @PostMapping(path = "/api/v1/transaction/{fromAccountNumber}/{toAccountNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return transfered amount", tags = "transaction")
    @ApiResponse(
            responseCode = "200",
            description = "Return transaction status",
            content = @Content(schema = @Schema(implementation = TransactionControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<Transaction> transferMoney(
            @Parameter(description = "From account number and To account number with the Amount", required = true)
            @RequestBody TransactionInput transfer);
}
