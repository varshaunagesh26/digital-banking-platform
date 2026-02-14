package com.db.account.controller;

import com.db.account.model.AccountDto;
import com.db.account.model.BranchDto;
import com.db.account.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/branches")
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    public BranchDto createBranch(@RequestBody BranchDto branchDto) throws Exception{
        return branchService.createBranch(branchDto);
    }

    @GetMapping
    public List<BranchDto> findAllBranches() throws Exception{
        return branchService.findAllBranches();
    }

    //TODO TEST if this works
    @GetMapping
    public List<AccountDto> findAllAccountsForBranch(@RequestParam("branchCode") Long branchCode) throws Exception{
        return branchService.findAllAccountsForABranch(branchCode);
    }

    @GetMapping("/{branchCode}")
    public BranchDto findBranchByBranchCode(@PathVariable("branchCode") long branchCode) throws Exception{
        return branchService.findBranchByBranchCode(branchCode);
    }

    @PatchMapping("/{branchCode}")
    public BranchDto updatePartiallyFromDto(@PathVariable("branchCode") long branchCode,@Valid @RequestBody BranchDto branchDto) throws Exception{
        return branchService.updatePartiallyFromDto(branchCode, branchDto);
    }

    @DeleteMapping("/{branchCode}")
    public void deleteBranch(@PathVariable("branchCode") long branchCode) throws Exception{
        branchService.deleteBranch(branchCode);
    }
}
