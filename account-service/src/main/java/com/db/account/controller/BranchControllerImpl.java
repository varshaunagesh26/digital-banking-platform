package com.db.account.controller;

import com.db.account.model.AccountDto;
import com.db.account.model.BranchDto;
import com.db.account.service.BranchService;
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
    public ResponseEntity<BranchDto> createBranch(BranchDto branchDto) throws Exception {
        return new ResponseEntity<>(
                branchService.createBranch(branchDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<BranchDto>> getAllBranches() throws Exception {
        return new ResponseEntity<>(
                branchService.getAllBranches(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AccountDto>> getAllAccountsForBranch(Long branchCode) throws Exception {
        return new ResponseEntity<>(
                branchService.getAllAccountsForABranch(branchCode), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BranchDto> getBranchByBranchCode(Long branchCode) throws Exception {
        return new ResponseEntity<>(
                branchService.getBranchByBranchCode(branchCode), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BranchDto> updateBranch(Long branchCode, BranchDto branchDto) throws Exception {
        return new ResponseEntity<>(
                branchService.updateBranch(branchCode, branchDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity deleteBranch(Long branchCode) throws Exception {
        branchService.deleteBranch(branchCode);
        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT);
    }
}
