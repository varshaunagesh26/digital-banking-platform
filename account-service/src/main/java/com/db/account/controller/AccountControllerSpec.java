package com.db.account.controller;

import com.db.account.model.AccountDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

public interface AccountControllerSpec {

    @PostMapping(path = "/api/v1/accounts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new Account for existing branch and customer", tags = "account")
    @ApiResponse(
            responseCode = "201",
            description = "Account created",
            content = @Content(schema = @Schema(implementation = AccountControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<AccountDto> createAccountForBranchAndCustomer(
            @RequestParam Long branchCode, @RequestParam Long customerId,
            @Validated @RequestBody AccountDto accountDto);


    @GetMapping(path = "/api/v1/accounts/{accountNumber}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return an account by accountNumber", tags = "account")
    @ApiResponse(
            responseCode = "200",
            description = "Return an account by accountNumber",
            content = @Content(schema = @Schema(implementation = AccountControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<AccountDto> getAccountByAccountNumber(
            @PathVariable Long accountNumber);


    @GetMapping(path = "/api/v1/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return all accounts", tags = "accounts")
    @ApiResponse(
            responseCode = "200",
            description = "Return all accounts",
            content = @Content(schema = @Schema(implementation = AccountControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<List<AccountDto>> getActiveAccounts();


    @PatchMapping(path = "/api/v1/accounts/{accountNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update account fields by accountNumber", tags = "account")
    @ApiResponse(
            responseCode = "200",
            description = "Account is updated",
            content = @Content(schema = @Schema(implementation = AccountControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<AccountDto> updateAccount(
            @Parameter(description = "Account number", required = true)
            @PathVariable Long accountNumber,
            @RequestBody AccountDto accountDto);

    @DeleteMapping("/api/v1/accounts/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete account by accountNumber", tags = "account")
    @ApiResponse(
            responseCode = "204",
            description = "Account deleted successfully",
            content = @Content(schema = @Schema(implementation = AccountControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<AccountDto> deleteAccount(
            @PathVariable Long accountNumber);
}
