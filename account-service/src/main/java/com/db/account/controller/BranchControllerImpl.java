package com.db.account.controller;

import com.db.account.service.BranchService;
import com.digital.backend.model.Account;
import com.digital.backend.model.Branch;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BranchControllerImpl implements BranchControllerSpec {

    private final BranchService branchService;

    @Override
    public ResponseEntity<Branch> createBranch(Branch branchDto){
        return new ResponseEntity<>(
                branchService.createBranch(branchDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<Branch>> getAllBranches(){
        return new ResponseEntity<>(
                branchService.getAllBranches(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Account>> getAllAccountsForBranch(Long branchCode){
        return new ResponseEntity<>(
                branchService.getAllAccountsForABranch(branchCode), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Branch> getBranchByBranchCode(Long branchCode){
        return new ResponseEntity<>(
                branchService.getBranchByBranchCode(branchCode), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Branch> updateBranch(Long branchCode, Branch branchDto){
        return new ResponseEntity<>(
                branchService.updateBranch(branchCode, branchDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity deleteBranch(Long branchCode){
        branchService.deleteBranch(branchCode);
        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT);
    }
}
