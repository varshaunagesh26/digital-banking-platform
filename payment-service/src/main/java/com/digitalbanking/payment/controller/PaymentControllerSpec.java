package digitalbanking.payment.controller;

import org.springframework.http.MediaType;


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
