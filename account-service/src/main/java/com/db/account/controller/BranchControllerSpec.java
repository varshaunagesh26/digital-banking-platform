package com.db.account.controller;


import com.db.account.model.AccountDto;
import com.db.account.model.BranchDto;
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

public interface BranchControllerSpec {

    @PostMapping(path = "/api/v1/branches",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new branch", tags = "branch")
    @ApiResponse(
            responseCode = "201",
            description = "Branch created",
            content = @Content(schema = @Schema(implementation = BranchControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<BranchDto> createBranch(
            @Validated @RequestBody BranchDto branchDto);


    @GetMapping(path = "/api/v1/branches",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return all the branches", tags = "branches")
    @ApiResponse(
            responseCode = "200",
            description = "Returned all the accounts",
            content = @Content(schema = @Schema(implementation = BranchControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<List<BranchDto>> getAllBranches();


    @GetMapping(path = "/api/v1/branches/{branchCode}/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return all accounts for a branch", tags = "accounts")
    @ApiResponse(
            responseCode = "200",
            description = "Returned all accounts for a branch",
            content = @Content(schema = @Schema(implementation = BranchControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<List<AccountDto>> getAllAccountsForBranch(
            @PathVariable Long branchCode);


    @GetMapping(path = "/api/v1/branches/{branchCode}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return branch by branch code", tags = "branch")
    @ApiResponse(
            responseCode = "200",
            description = "Returned branch by branch code",
            content = @Content(schema = @Schema(implementation = BranchControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<BranchDto> getBranchByBranchCode(
            @PathVariable Long branchCode);


    @PatchMapping(path = "/api/v1/branches/{branchCode}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update branch by branch code", tags = "branch")
    @ApiResponse(
            responseCode = "200",
            description = "Branch is updated",
            content = @Content(schema = @Schema(implementation = BranchControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<BranchDto> updateBranch(
            @Parameter(description = "Branch Code", required = true)
            @PathVariable Long branchCode,
            @RequestBody BranchDto branchDto);


    @DeleteMapping("/api/v1/branches/{branchCode}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete branch", tags = "branch")
    @ApiResponse(
            responseCode = "204",
            description = "Branch deleted",
            content = @Content(schema = @Schema(implementation = BranchControllerImpl.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<BranchDto> deleteBranch(@PathVariable Long branchCode);
}
